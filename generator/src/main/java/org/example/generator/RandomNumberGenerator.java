package org.example.generator;

import org.example.generator.util.DataGenerator;

public class RandomNumberGenerator<T extends DataGenerator> {
    public static int sensorIdCounter_;
    public static int randomNumberAmount_;
    public static int min_;
    public static int max_;

    public RandomNumberGenerator(int randomNumberAmount, int min, int max) {
        sensorIdCounter_ = -1;
        randomNumberAmount_ = randomNumberAmount;
        min_ = min;
        max_ = max;
    }

    public String generate() {
        String timestamp = generateTimestamp();
        String randomNumberString = generateRandomNumberString();
        return timestamp + randomNumberString;
    }

    private static String generateTimestamp() {
        return String.valueOf(System.currentTimeMillis());
    }

    private static String generateRandomNumberString() {

        String myString = "";
        for (int i = 0; i < randomNumberAmount_; i++) {
            myString += "," + String.valueOf(((long) (Math.random() * (max_ - min_ + 1))) + min_);
        }
        return myString;
    }
}


