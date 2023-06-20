package stc.monitoring.financebot.service;

public class Util {

    public static Integer checkIfCorrectPositiveNumber (String number) throws NumberFormatException{
        int out = Integer.parseInt(number);
        if (out < 0) {
            out = out*-1;
        }
        return out;
    }
}
