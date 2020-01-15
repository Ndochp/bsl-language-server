/*
 * This file is a part of BSL Language Server.
 *
 * Copyright © 2018-2020
 * Alexey Sosnoviy <labotamy@gmail.com>, Nikita Gryzlov <nixel2007@gmail.com> and contributors
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 *
 * BSL Language Server is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * BSL Language Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with BSL Language Server.
 */
package com.github._1c_syntax.bsl.languageserver.context.computer;

import com.github._1c_syntax.bsl.languageserver.context.DocumentContext;
import com.github._1c_syntax.bsl.languageserver.context.symbol.MethodSymbol;
import com.github._1c_syntax.bsl.languageserver.utils.Ranges;
import com.github._1c_syntax.bsl.languageserver.utils.Trees;
import com.github._1c_syntax.bsl.parser.BSLParser;
import com.github._1c_syntax.bsl.parser.BSLParserBaseListener;
import com.github._1c_syntax.bsl.parser.BSLParserRuleContext;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.Tree;
import org.eclipse.lsp4j.Range;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

// idea from https://pdepend.org/documentation/software-metrics/cyclomatic-complexity.html
public class CyclomaticComplexityComputer
  extends BSLParserBaseListener
  implements Computer<CyclomaticComplexityComputer.Data> {

  private final DocumentContext documentContext;

  private int fileComplexity;
  private int fileCodeBlockComplexity;
  private List<SecondaryLocation> fileBlockComplexitySecondaryLocations;

  private Map<MethodSymbol, Integer> methodsComplexity;
  private Map<MethodSymbol, List<SecondaryLocation>> methodsComplexitySecondaryLocations;

  private MethodSymbol currentMethod;
  private int complexity;
  private Set<BSLParserRuleContext> ignoredContexts;

  public CyclomaticComplexityComputer(DocumentContext documentContext) {
    this.documentContext = documentContext;
    fileComplexity = 0;
    fileCodeBlockComplexity = 0;
    fileBlockComplexitySecondaryLocations = new ArrayList<>();
    resetMethodComplexityCounters();
    methodsComplexity = new HashMap<>();
    methodsComplexitySecondaryLocations = new HashMap<>();
    ignoredContexts = new HashSet<>();
  }

  @Override
  public Data compute() {
    fileComplexity = 0;
    fileCodeBlockComplexity = 0;
    resetMethodComplexityCounters();
    methodsComplexity.clear();
    ignoredContexts.clear();

    ParseTreeWalker walker = new ParseTreeWalker();
    walker.walk(this, documentContext.getAst());

    return new Data(
      fileComplexity,
      fileCodeBlockComplexity,
      fileBlockComplexitySecondaryLocations,
      methodsComplexity,
      methodsComplexitySecondaryLocations
    );
  }

  @Override
  public void enterSub(BSLParser.SubContext ctx) {
    Optional<MethodSymbol> methodSymbol = documentContext.getMethodSymbol(ctx);
    if (!methodSymbol.isPresent()) {
      return;
    }
    resetMethodComplexityCounters();
    currentMethod = methodSymbol.get();
    complexityIncrement(ctx.getStart());

    super.enterSub(ctx);
  }

  @Override
  public void exitSub(BSLParser.SubContext ctx) {
    incrementFileComplexity();
    if (currentMethod != null) {
      methodsComplexity.put(currentMethod, complexity);
    }
    currentMethod = null;
    ignoredContexts.clear();
    super.exitSub(ctx);
  }

  @Override
  public void enterFileCodeBlockBeforeSub(BSLParser.FileCodeBlockBeforeSubContext ctx) {
    resetMethodComplexityCounters();
    super.enterFileCodeBlockBeforeSub(ctx);
  }

  @Override
  public void exitFileCodeBlockBeforeSub(BSLParser.FileCodeBlockBeforeSubContext ctx) {
    incrementFileComplexity();
    incrementFileCodeBlockComplexity();
    ignoredContexts.clear();
    super.exitFileCodeBlockBeforeSub(ctx);
  }

  @Override
  public void enterFileCodeBlock(BSLParser.FileCodeBlockContext ctx) {
    resetMethodComplexityCounters();
    super.enterFileCodeBlock(ctx);
  }

  @Override
  public void exitFileCodeBlock(BSLParser.FileCodeBlockContext ctx) {
    incrementFileComplexity();
    incrementFileCodeBlockComplexity();
    ignoredContexts.clear();
    super.exitFileCodeBlock(ctx);
  }

  @Override
  public void enterIfBranch(BSLParser.IfBranchContext ctx) {
    complexityIncrement(ctx.IF_KEYWORD().getSymbol());
    super.enterIfBranch(ctx);
  }

  @Override
  public void enterElsifBranch(BSLParser.ElsifBranchContext ctx) {
    complexityIncrement(ctx.ELSIF_KEYWORD().getSymbol());
    super.enterElsifBranch(ctx);
  }

  @Override
  public void enterElseBranch(BSLParser.ElseBranchContext ctx) {
    complexityIncrement(ctx.ELSE_KEYWORD().getSymbol());
    super.enterElseBranch(ctx);
  }

  @Override
  public void enterTernaryOperator(BSLParser.TernaryOperatorContext ctx) {
    complexityIncrement(ctx.QUESTION().getSymbol());
    super.enterTernaryOperator(ctx);
  }

  @Override
  public void enterForEachStatement(BSLParser.ForEachStatementContext ctx) {
    complexityIncrement(ctx.FOR_KEYWORD().getSymbol());
    super.enterForEachStatement(ctx);
  }

  @Override
  public void enterForStatement(BSLParser.ForStatementContext ctx) {
    complexityIncrement(ctx.FOR_KEYWORD().getSymbol());
    super.enterForStatement(ctx);
  }

  @Override
  public void enterWhileStatement(BSLParser.WhileStatementContext ctx) {
    complexityIncrement(ctx.WHILE_KEYWORD().getSymbol());
    super.enterWhileStatement(ctx);
  }

  @Override
  public void enterExceptCodeBlock(BSLParser.ExceptCodeBlockContext ctx) {
    complexityIncrement(((BSLParser.TryStatementContext) ctx.getParent()).EXCEPT_KEYWORD().getSymbol());
    super.enterExceptCodeBlock(ctx);
  }

  @Override
  public void enterGlobalMethodCall(BSLParser.GlobalMethodCallContext ctx) {
    BSLParser.MethodNameContext methodNameContext = ctx.methodName();
    if (methodNameContext != null && currentMethod != null) {
      String calledMethodName = methodNameContext.getText();
      if (currentMethod.getName().equalsIgnoreCase(calledMethodName)) {
        complexityIncrement(methodNameContext.IDENTIFIER().getSymbol());
      }
    }

    super.enterGlobalMethodCall(ctx);
  }

  @Override
  public void enterGotoStatement(BSLParser.GotoStatementContext ctx) {
    complexityIncrement(ctx.GOTO_KEYWORD().getSymbol());
    super.enterGotoStatement(ctx);
  }

  @Override
  public void enterExpression(BSLParser.ExpressionContext ctx) {

    if (ignoredContexts.contains(ctx)) {
      return;
    }

    final List<Token> flattenExpression = flattenExpression(ctx);

    int emptyTokenType = -1;
    AtomicInteger lastOperationType = new AtomicInteger(emptyTokenType);

    flattenExpression.forEach((Token token) -> {
      int currentOperationType = token.getType();
      if (lastOperationType.get() != currentOperationType) {
        lastOperationType.set(currentOperationType);
        if (currentOperationType != emptyTokenType) {
          complexityIncrement(token);
        }
      }
    });

    super.enterExpression(ctx);
  }

  private List<Token> flattenExpression(BSLParser.ExpressionContext ctx) {

    ignoredContexts.add(ctx);

    List<Token> result = new ArrayList<>();

    final List<Tree> children = Trees.getChildren(ctx);
    for (Tree tree : children) {
      if (!(tree instanceof BSLParserRuleContext)) {
        continue;
      }

      BSLParserRuleContext parserRule = ((BSLParserRuleContext) tree);
      if (parserRule instanceof BSLParser.MemberContext) {
        flattenMember(result, (BSLParser.MemberContext) parserRule);
      } else if (parserRule instanceof BSLParser.OperationContext) {
        flattenOperation(result, (BSLParser.OperationContext) parserRule);
      }
    }

    return result;
  }

  private void flattenMember(List<Token> result, BSLParser.MemberContext member) {
    final BSLParser.ExpressionContext expression = member.expression();

    if (expression == null) {
      return;
    }

    final List<Token> nestedTokens = flattenExpression(expression);
    if (nestedTokens.isEmpty()) {
      return;
    }

    final BSLParser.UnaryModifierContext unaryModifier = member.unaryModifier();

    if (unaryModifier != null && unaryModifier.NOT_KEYWORD() != null) {
      final CommonToken splitter = new CommonToken(-1);
      result.add(splitter);
      result.addAll(nestedTokens);
      result.add(splitter);
    } else {
      result.addAll(nestedTokens);
    }
  }

  private void flattenOperation(List<Token> result, BSLParser.OperationContext operation) {
    final BSLParser.BoolOperationContext boolOperation = operation.boolOperation();

    if (boolOperation != null) {
      result.add(boolOperation.getStart());
    }
  }

  private void resetMethodComplexityCounters() {
    complexity = 0;
  }

  private void incrementFileComplexity() {
    fileComplexity += complexity;
  }

  private void incrementFileCodeBlockComplexity() {
    fileCodeBlockComplexity += complexity;
  }

  private void complexityIncrement(Token token) {
    complexity += 1;
    addSecondaryLocation(token);
  }

  private void addSecondaryLocation(Token token) {
    String message;
    message = String.format("+%d", 1);
    SecondaryLocation secondaryLocation = new SecondaryLocation(Ranges.create(token), message.intern());
    List<SecondaryLocation> locations;
    if (currentMethod != null) {
      locations = methodsComplexitySecondaryLocations.computeIfAbsent(
        currentMethod,
        (MethodSymbol methodSymbol) -> new ArrayList<>()
      );
    } else {
      locations = fileBlockComplexitySecondaryLocations;
    }

    locations.add(secondaryLocation);
  }

  @Value
  @AllArgsConstructor
  public static class SecondaryLocation {
    private final Range range;
    private final String message;
  }

  @Value
  @AllArgsConstructor
  public static class Data {
    private final int fileComplexity;
    private final int fileCodeBlockComplexity;
    private List<SecondaryLocation> fileBlockComplexitySecondaryLocations;

    private final Map<MethodSymbol, Integer> methodsComplexity;
    private Map<MethodSymbol, List<SecondaryLocation>> methodsComplexitySecondaryLocations;
  }
}