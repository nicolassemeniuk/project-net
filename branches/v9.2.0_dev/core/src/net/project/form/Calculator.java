/* 
 * Copyright 2000-2009 Project.net Inc.
 *
 * This file is part of Project.net.
 * Project.net is free software: you can redistribute it and/or modify it under the terms of 
 * the GNU General Public License as published by the Free Software Foundation, version 3 of the License.
 * 
 * Project.net is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Project.net.
 * If not, see http://www.gnu.org/licenses/gpl-3.0.html
*/

 package net.project.form;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Calculates the result of a CalculationField.
 *
 * @author Tim Morrow
 * @since Version 7.5.1
 */
class Calculator {

    //
    // Static Members
    //

    /**
     * The scale to round to in divide operations.
     */
    private static final int SCALE = 5;

    /**
     * Maintains the relative precedence of all operators.
     * Each key is an operator, each value is an Integer where a lower Integer
     * value means a higher precedence.
     */
    private static final Map OPERATOR_RELATIVE_PRECEDENCE = new HashMap();

    static {
        int precedenceValue = 0;

        // Divide and Multiply have equal precedence
        OPERATOR_RELATIVE_PRECEDENCE.put(Operator.DIVIDE, new Integer(precedenceValue));
        OPERATOR_RELATIVE_PRECEDENCE.put(Operator.MULTIPLY, new Integer(precedenceValue));

        // Plus and Minus have equal precedence
        precedenceValue++;
        OPERATOR_RELATIVE_PRECEDENCE.put(Operator.PLUS, new Integer(precedenceValue));
        OPERATOR_RELATIVE_PRECEDENCE.put(Operator.MINUS, new Integer(precedenceValue));
    }

    //
    // Instance Members
    //

    /**
     * The formula elements in infix order.
     */
    private final Collection infixFormula;

    /**
     * The form providing the fields referred to by operands in the formula.
     */
    private final Form form;

    /**
     * The data providing the values for the fields referred to by operands
     * in the formula.
     */
    private final FormData formData;

    /**
     * Creates a new Calculator based on the specified formula using the
     * specified data to get the values for the calculation.
     *
     * @param formula the formula that provides the operand references and operators
     * for the calculation
     * @param form the current form instance, required to lookup fields
     * referred to by operands in the formula
     * @param formData the data that provides the values for the operand
     * references in the formula
     */
    Calculator(Formula formula, Form form, FormData formData) {
        // Reverse the formula to put its elements in infix order
        this.infixFormula = formula.getReversedElements();
        this.form = form;
        this.formData = formData;
    }


    /**
     * Calculates the result of the formula
     * @return the calculated result
     * @throws CalculationField.FormulaException if the formula is empty
     * @throws CalculationField.DivideByZeroException if an attempt was made
     * to divide by zero while calculating
     * @throws CalculationField.CalculationException if a problem occurred
     * evaluating an operand or the formula is empty
     */
    BigDecimal calculateResult()  throws CalculationField.FormulaException, CalculationField.DivideByZeroException, CalculationField.CalculationException {

        if (this.infixFormula.isEmpty()) {
            throw new CalculationField.FormulaException("Formula is empty");
        }

        // Convert the formula to postfix
        // The postfixFormula still maintains Operand and Operators
        List postfixFormula = buildPostfixFormula();

        // Now evaluate the formula and return the result
        return evaluatePostfixFormula(postfixFormula);
    }

    /**
     * Builds a postfix formula from the current formula.
     * @return an ordered sequence of operands and operators representing the
     * formula in postfix order
     */
    private List buildPostfixFormula() {
        List postfixFormula = new ArrayList();

        Stack operatorStack = new Stack();

        // Iterate over each (infix) formula element, building the postfix
        // formula with the operators and operands
        for (Iterator it = this.infixFormula.iterator(); it.hasNext(); ) {
            IFormulaElement nextElement = (IFormulaElement) it.next();

            if (nextElement.getElementType().equals(Formula.OPERAND)) {
                // We have an Operand
                // Add it to the formula
                postfixFormula.add(nextElement);

            } else {
                // We have an Operator
                // We must add all higher (or equal) precedence operators to the formula
                // then save the current operator on the stack
                while (!operatorStack.isEmpty() && comparePrecedence((Operator) operatorStack.peek(), (Operator) nextElement) >= 0) {
                    Operator higherPrecedenceOperator = (Operator) operatorStack.pop();
                    postfixFormula.add(higherPrecedenceOperator);
                }
                operatorStack.push(nextElement);
            }

        }

        // Now pop the remaining operators off the stack and add to the end
        // of the postfixFormula formula
        while (!operatorStack.isEmpty()) {
            IFormulaElement nextElement = (IFormulaElement) operatorStack.pop();
            postfixFormula.add(nextElement);
        }

        return postfixFormula;
    }

    /**
     * Evaluates the postfixFormula containing operators and operands and
     * returns the result.
     * @param postfixFormula the formula
     * @return the result of the calculation
     * @throws CalculationField.DivideByZeroException if an attempt is made
     * to divide by zero; this may occur if an operand value is zero or empty
     * @throws CalculationField.CalculationException if an unknown operator is
     * encountered
     */
    private BigDecimal evaluatePostfixFormula(List postfixFormula) throws CalculationField.CalculationException {

        // The value stack contains BigDecimal values
        Stack valueStack = new Stack();

        for (Iterator it = postfixFormula.iterator(); it.hasNext();) {
            IFormulaElement nextElement = (IFormulaElement) it.next();

            if (nextElement.getElementType().equals(Formula.OPERAND)) {
                // We have an Operand (value)

                // Add its value to the stack
                valueStack.push(getValueForOperand((Operand) nextElement));

            } else {
                // We have an Operator

                // Perform the caclulation for the operator
                if (((Operator) nextElement).isBinary()) {
                    // A binary operator has two operands
                    // The right hand value is the LAST value on the stack
                    // so we have to pop it first
					if (valueStack.size() < 2) {
						valueStack.push(new BigDecimal(0));
					} else {
						BigDecimal rightValue = (BigDecimal) valueStack.pop();
						BigDecimal leftValue = (BigDecimal) valueStack.pop();
						valueStack.push(calculateBinaryOperation((Operator) nextElement, leftValue, rightValue));
					}
                } else {
                    // A unary operator has one operand
                    BigDecimal value = (BigDecimal) valueStack.pop();
                    valueStack.push(calculateUnaryOperation((Operator) nextElement, value));

                }
            }
        }

        // The last remaining value on the stack is the result
        BigDecimal result = (BigDecimal) valueStack.pop();
        return result;
    }

    /**
     * Compares the precedence of two operators.
     * @param firstOperator the first operator to compare
     * @param secondOperator the second operator to compare
     * @return -1 if the first operator has a lower precedence than the second
     * operator; 0 if operators have equal precedence; 1 if the first operator
     * has a higher precedence than the second operator
     */
    private int comparePrecedence(Operator firstOperator, Operator secondOperator) {

        int firstOperatorPrecedence = ((Integer) OPERATOR_RELATIVE_PRECEDENCE.get(firstOperator.getValue())).intValue();
        int secondOperatorPrecedence = ((Integer) OPERATOR_RELATIVE_PRECEDENCE.get(secondOperator.getValue())).intValue();

        // Now compare the precedence; note that a lower precedence value
        // actually means a higher precedence since precedence values are
        // incremented from 0 with 0 being the highest precedence
        int comparisonResult;

        if (firstOperatorPrecedence < secondOperatorPrecedence) {
            // First Operator has higher precedence
            comparisonResult = 1;

        } else if (firstOperatorPrecedence == secondOperatorPrecedence) {
            // Equal precedence
            comparisonResult = 0;

        } else {
            // First Operator has lower precedence
            comparisonResult = 1;
        }

        return comparisonResult;
    }

    /**
     * Returns the value for the specified operand using the current formData.
     * This is essentially the field value of the field represented by the
     * operand.  It assumes that the operand points to a NumberField.
     *
     * @param operand the operand for which to get the value
     * @return the value for the operand or zero if no value was found
     * for the operand
     * @throws CalculationField.CalculationException if there is a problem
     * getting the value for an operand; for example, the operand refers to
     * a FormField that is not a NumberField
     */
    private BigDecimal getValueForOperand(Operand operand)
            throws CalculationField.CalculationException {

        BigDecimal value = new BigDecimal("0");

        String formFieldID = operand.getValue();
        if (formFieldID != null && !formFieldID.equals("")) {

            // Grab the formField for the fieldID from the current list view
            FormField formField = this.form.getField(formFieldID);
            if (!(formField instanceof NumberField)) {
                throw new CalculationField.CalculationException("Expected number field in calculation for field ID " + formFieldID);
            }

            // Grab the fieldData for that formfield from the current formdata
            FieldData fieldData = (FieldData) this.formData.get(formField.getSQLName());

            if (fieldData != null && fieldData.size() > 0) {
                // We have to assume that the field is a number
                Number number = ((NumberField) formField).getNumber(fieldData);
                if (number != null) {
                    value = new BigDecimal(number.doubleValue());
                }
            }
        }

        return value;
    }

    /**
     * Performs a binary operation on the specified operands.
     * @param operator the binary operation to perform
     * @param x the first operand
     * @param y the second operand
     * @return the result of performing the binary operation
     * @throws CalculationField.DivideByZeroException if there was an attempt
     * to divide by zero
     * @throws CalculationField.CalculationException if the operator was
     * not a known binary operator
     */
    private BigDecimal calculateBinaryOperation(Operator operator, BigDecimal x, BigDecimal y)
            throws CalculationField.CalculationException {

        BigDecimal result;

        if (operator.getValue().equals(Operator.DIVIDE)) {

            if (y.compareTo(new BigDecimal("0")) == 0) {
                throw new CalculationField.DivideByZeroException("Attempted to divide by zero");
            }

            result = x.divide(y, SCALE, BigDecimal.ROUND_HALF_UP);

        } else if (operator.getValue().equals(Operator.MULTIPLY)) {
            result = x.multiply(y);

        } else if (operator.getValue().equals(Operator.PLUS)) {
            result = x.add(y);

        } else if (operator.getValue().equals(Operator.MINUS)) {
            result = x.subtract(y);

        } else {
            // Unknown operator
            throw new CalculationField.CalculationException("Unkown binary operator " + operator.getValue());
        }

        return result;
    }

    /**
     * Performs a unary operation on the specified operand.
     * Currently always throws an exception since no unary operations are
     * supported.
     * @param operator the unary operation to perform
     * @param x the operand
     * @return the result of performing the unary operation
     * @throws CalculationField.CalculationException if the operator was not
     * a known unary operator
     */
    private BigDecimal calculateUnaryOperation(Operator operator, BigDecimal x)
            throws CalculationField.CalculationException {

        throw new CalculationField.CalculationException("Unknown unary operator " + operator.getValue());
    }

}
