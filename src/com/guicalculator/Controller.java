package com.guicalculator;

import com.guicalculator.logic.CalculatorLogic;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class Controller {
    private StringBuilder numberAsString;
    private double firstNumber;
    private double secondNumber;
    private double numberBeforeOrderOfOperationChange;
    private Operator currentOperator;
    private Operator operatorBeforeOrderOfOperationChange;

    private boolean equalsPressed;
    private boolean isEqualsButtonRepeated;
    private boolean isAnotherOperatorUsedInsteadOfEquals;
    private boolean hasOrderOfOperationsChanged;
    private boolean blockRepeatedOperatorPressBeforeAnotherNumberInput;

    @FXML
    private TextField numberField;
    @FXML
    private Button divideButton;
    @FXML
    private Button sevenButton;
    @FXML
    private Button eightButton;
    @FXML
    private Button nineButton;
    @FXML
    private Button multiplyButton;
    @FXML
    private Button fourButton;
    @FXML
    private Button fiveButton;
    @FXML
    private Button sixButton;
    @FXML
    private Button subtractButton;
    @FXML
    private Button oneButton;
    @FXML
    private Button twoButton;
    @FXML
    private Button threeButton;
    @FXML
    private Button additionButton;
    @FXML
    private Button zeroButton;

    public void initialize() {
        numberAsString = new StringBuilder();
        currentOperator = Operator.NOTHING;
        operatorBeforeOrderOfOperationChange = Operator.NOTHING;
        equalsPressed = false;
        isEqualsButtonRepeated = false;
        isAnotherOperatorUsedInsteadOfEquals = false;
        blockRepeatedOperatorPressBeforeAnotherNumberInput = false;
        hasOrderOfOperationsChanged = false;
    }

    public void handleNumberButtonPress() {
        if (equalsPressed) {
            handleACButton();
            equalsPressed = false;
        }

        if (sevenButton.isPressed()) {
            numberAsString.append(7);
        } else if (eightButton.isPressed()) {
            numberAsString.append(8);
        } else if (nineButton.isPressed()) {
            numberAsString.append(9);
        } else if (fourButton.isPressed()){
            numberAsString.append(4);
        } else if (fiveButton.isPressed()){
            numberAsString.append(5);
        } else if (sixButton.isPressed()){
            numberAsString.append(6);
        } else if (oneButton.isPressed()){
            numberAsString.append(1);
        } else if (twoButton.isPressed()){
            numberAsString.append(2);
        } else if (threeButton.isPressed()){
            numberAsString.append(3);
        }  else if (zeroButton.isPressed()){
            numberAsString.append(0);
        }

        numberField.setText(numberAsString.toString());
        blockRepeatedOperatorPressBeforeAnotherNumberInput = false;
    }

    //Handles the Divide, Multiply, Subtract and Addition buttons.
    public void handleOperatorButtonPress() {
        isEqualsButtonRepeated = false;
        Operator tempOperator = Operator.NOTHING;

        if (divideButton.isPressed()) {
            tempOperator = Operator.DIVIDE;
        } else if (multiplyButton.isPressed()) {
            tempOperator = Operator.MULTIPLY;
        } else if (subtractButton.isPressed()) {
            tempOperator = Operator.SUBTRACT;
        } else if (additionButton.isPressed()) {
            tempOperator = Operator.ADD;
        }

        if (!blockRepeatedOperatorPressBeforeAnotherNumberInput && tempOperator != Operator.NOTHING) {
            operatorChange(tempOperator);
        }
    }

    public void handleACButton() {
        numberAsString.setLength(0);
        numberField.setText("0");
        currentOperator = Operator.NOTHING;
        operatorBeforeOrderOfOperationChange = Operator.NOTHING;
        isAnotherOperatorUsedInsteadOfEquals = false;
        isEqualsButtonRepeated = false;
        firstNumber = 0;
        secondNumber = 0;
    }


    public void handlePositiveNegativeButton() {
        if (numberAsString.toString().isEmpty()) {
            return;
        }

        double currentNumber = Double.parseDouble(numberAsString.toString());
        if (Math.signum(currentNumber) == 1) {
            numberAsString.insert(0, '-');
        } else if (Math.signum(currentNumber) == -1){
            numberAsString.deleteCharAt(0);
        }
        formatNumberFieldText(Double.parseDouble(numberAsString.toString()));
    }

    public void handlePercentButton() {
        if (Double.parseDouble(numberAsString.toString()) == 0 || Double.parseDouble(numberField.getText()) == 0) {
            return;
        }

        if (currentOperator != Operator.NOTHING) {
            double percentage = Double.parseDouble(numberAsString.toString());
            double percentageOfOriginalNumber = CalculatorLogic.percent(firstNumber, percentage);

            formatNumberFieldText(percentageOfOriginalNumber);
            numberAsString.setLength(0);
            numberAsString.append(percentageOfOriginalNumber);
            numberField.setText(numberAsString.toString());
            System.out.println(percentageOfOriginalNumber);
        } else {
            double number = Double.parseDouble(numberField.getText()) / 100;
            numberField.setText(String.valueOf(number));
        }
    }

    public void handlePointButton() {
        if (numberAsString.toString().isEmpty() || numberAsString.toString().contains(".")) {
            return;
        }
        numberAsString.append('.');
        numberField.setText(numberAsString.toString());
    }

    //The purpose of the equalsPressed boolean is so that isSecondaryCalculationToMake() will only become true once
    //equals is pressed (other than when continuously using other operators instead of equals).  It is also used to
    //allow a new calculation to be made when pressing any number button.
    public void handleEqualsButton() {
        equalsPressed = true;
        if (currentOperator == Operator.NOTHING) {
            return;
        }

        handleCalculation();

        if (!isEqualsButtonRepeated) {
            isEqualsButtonRepeated = true;
        }

        numberAsString.setLength(0);
        numberAsString.append(numberField.getText());
        isAnotherOperatorUsedInsteadOfEquals = false;
        hasOrderOfOperationsChanged = false;
    }

    //Deals with the basic calculation
    private double calculate(Operator operator) {
        double calculation;

        if (operator == Operator.SUBTRACT) {
            calculation = CalculatorLogic.subtract(firstNumber, secondNumber);
        } else if (operator == Operator.MULTIPLY){
            calculation = CalculatorLogic.multiply(firstNumber, secondNumber);
        } else if (operator == Operator.DIVIDE) {
            calculation = CalculatorLogic.divide(firstNumber, secondNumber);
        } else {
            calculation = CalculatorLogic.add(firstNumber, secondNumber);
        }

        return calculation;
    }

    private void handleCalculation(Operator operator) {
        Operator operatorToCalculateWith = operator;
        double secondNumberBeforeSecondaryCalculationMade = 0;
        boolean secondaryCalculationHasBeenMade = false;

        if (isSecondaryCalculationToMake(operator)) {
            secondNumber = Double.parseDouble(numberAsString.toString());
            secondNumberBeforeSecondaryCalculationMade = secondNumber;
            secondNumber = calculate(currentOperator);
            firstNumber = numberBeforeOrderOfOperationChange;

            operatorToCalculateWith = operatorBeforeOrderOfOperationChange;
            operatorBeforeOrderOfOperationChange = Operator.NOTHING;
            secondaryCalculationHasBeenMade = true;

        } else if (numberAsString.toString().isEmpty()) {
            secondNumber = firstNumber;
        } else if (isEqualsButtonRepeated) {
            //This is empty so that secondNumber doesn't change.
        } else {
            secondNumber = Double.parseDouble(numberAsString.toString());
        }

        firstNumber = calculate(operatorToCalculateWith);
        formatNumberFieldText(firstNumber);
        blockRepeatedOperatorPressBeforeAnotherNumberInput = false;

        if (secondaryCalculationHasBeenMade) {
            secondNumber = secondNumberBeforeSecondaryCalculationMade;
        }
    }

    //The reason I've overloaded this method is so that operatorChange can deal with calculating with the newOperator
    //when the order of operations has been changed, before setting it as currentOperator.
    private void handleCalculation() {
        handleCalculation(currentOperator);
    }

    //Formats the numberField to display a non-decimal number if it's an integer.
    private void formatNumberFieldText(double number) {
//        double number = Double.parseDouble(numberString);
        if (number % 1 == 0) {
            int numberAsInt = (int) number;
            numberField.setText(String.valueOf(numberAsInt));
        } else {
            numberField.setText(String.valueOf(number));
        }
    }

    private boolean isNewOrderOfOperationTheSameAsPrevious(Operator newOperator) {
        if (newOperator == Operator.ADD || newOperator == Operator.SUBTRACT) {
            if (currentOperator == Operator.ADD || currentOperator == Operator.SUBTRACT) {
                return true;
            }

        } else if (newOperator == Operator.MULTIPLY || newOperator == Operator.DIVIDE) {
            if (currentOperator == Operator.MULTIPLY || currentOperator == Operator.DIVIDE) {
                return true;
            }
        }

        return false;
    }

    //Deals with validating operator changes and handling any calculations necessary, for example when order of operations
    //has changed.
    private void operatorChange(Operator newOperator) {
        if (isAnotherOperatorUsedInsteadOfEquals && isNewOrderOfOperationTheSameAsPrevious(newOperator)) {
            handleCalculation();
            hasOrderOfOperationsChanged = false;

        } else if (isAnotherOperatorUsedInsteadOfEquals) {
            boolean newOperatorIsAddOrSubtract = (newOperator == Operator.SUBTRACT || newOperator == Operator.ADD);
            hasOrderOfOperationsChanged = true;

            if (newOperatorIsAddOrSubtract) {
                if (operatorBeforeOrderOfOperationChange == Operator.NOTHING) {
                    handleCalculation();
                } else {
                    handleCalculation(newOperator);
                    operatorBeforeOrderOfOperationChange = Operator.NOTHING;
                }

            } else {
                numberBeforeOrderOfOperationChange = firstNumber;
                operatorBeforeOrderOfOperationChange = currentOperator;
            }

        } else {
            isAnotherOperatorUsedInsteadOfEquals = true;
            hasOrderOfOperationsChanged = false;
        }

        firstNumber = Double.parseDouble(numberField.getText());
        numberAsString.setLength(0);

        currentOperator = newOperator;
        blockRepeatedOperatorPressBeforeAnotherNumberInput = true;
    }

    //Checks to see if there's a secondary calculation to make, as there would be when, for example, changing
    //order of operations from +/- to x/÷
    private boolean isSecondaryCalculationToMake(Operator operator) {
        if (numberAsString.toString().isEmpty() || operatorBeforeOrderOfOperationChange == Operator.NOTHING) {
            return false;
        }

        boolean isOperatorAddOrSubtract = (operator == Operator.ADD || operator == Operator.SUBTRACT);
        if (hasOrderOfOperationsChanged && isOperatorAddOrSubtract) {
            return true;
        }

        else if (equalsPressed) {
            return true;
        }

        return false;
    }

}
