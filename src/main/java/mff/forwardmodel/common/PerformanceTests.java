package mff.forwardmodel.common;

import engine.core.MarioForwardModel;
import engine.core.MarioWorld;
import engine.helper.MarioActions;
import mff.forwardmodel.bin.core.MarioForwardModelBin;
import mff.forwardmodel.slim.core.MarioForwardModelSlim;
import sun.misc.Unsafe;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * Copyright notice and permission notice for the getUnsafe() method.
 *
 * *******************************************************************************
 *  * The MIT License (MIT)//  w  w  w. j a  v a 2 s . c  o m
 *  *
 *  * Copyright (c) 2016 Dalibor Drgo? <emptychannelmc@gmail.com>
 *  *
 *  * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  * of this software and associated documentation files (the "Software"), to deal
 *  * in the Software without restriction, including without limitation the rights
 *  * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  * copies of the Software, and to permit persons to whom the Software is
 *  * furnished to do so, subject to the following conditions:
 *  *
 *  * The above copyright notice and this permission notice shall be included in
 *  * all copies or substantial portions of the Software.
 *  *
 *  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  * THE SOFTWARE.
 *  ******************************************************************************
 *
 * code source:
 * http://www.java2s.com/example/java-utility-method/byte-array-copy/memcpy-object-to-object-from-int-size-3e6c7.html
 */

public class PerformanceTests {
    private static Unsafe unsafe;

    private static Unsafe getUnsafe() {
        if (unsafe == null) {
            Class<?> clz = Unsafe.class;
            Field[] fields = clz.getDeclaredFields();
            for (Field f : fields) {
                if (!f.getType().equals(Unsafe.class)) {
                    continue;
                }
                try {
                    f.setAccessible(true);
                    Unsafe unf = (Unsafe) f.get(null);
                    if (unf != null) {
                        return (unsafe = unf);
                    }
                } catch (Throwable ignored) {
                }
            }
        }
        return unsafe;
    }

    private static String getLevel(String filepath) {
        String content = "";
        try {
            content = new String(Files.readAllBytes(Paths.get(filepath)));
            return content;
        } catch (IOException ignored) {
            // try with working directory set one folder down
        }
        try {
            content = new String(Files.readAllBytes(Paths.get("." + filepath)));
        }
        catch (IOException e) {
            System.out.println("Level couldn't be loaded, please check the path provided with regards to your working directory.");
            System.exit(1);
        }
        return content;
    }

    public static void main(String[] args) {
        //testArrayCopies();
        testClones();
        //testArraysCreation();
        //testOneArrayVsFour();
        //testOneArrayVsTwo();
        //testCopySpeedDiff();
        //testCloneAndConvertVsAdvance();
    }

    private static void testCloneAndConvertVsAdvance() {
        String level = getLevel("./levels/original/lvl-1.txt");
        MarioWorld world = new MarioWorld(null);
        world.visuals = false;
        world.initializeLevel(level, 1000 * 200);
        world.mario.isLarge = false;
        world.mario.isFire = false;
        world.update(new boolean[MarioActions.numberOfActions()]);

        long time;
        long duration;

        MarioForwardModelSlim slimModel = Converter.originalToSlim(new MarioForwardModel(world.clone()), 27);

        time = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            MarioForwardModelSlim test = Converter.originalToSlim(new MarioForwardModel(world.clone()), 27);
        }
        duration = System.currentTimeMillis() - time;
        System.out.println("Clone + convert: " + duration + " ms");

        boolean[] action = { true, false, false, false, true };

        time = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            slimModel.advance(action);
        }
        duration = System.currentTimeMillis() - time;
        System.out.println("Advance: " + duration + " ms");
    }

    private static void testCopySpeedDiff() {
        int size1 = 5;
        int size2 = 20;

        int[] ints1 = new int[size1];
        int[] ints2 = new int[size2];
        for (int i = 0; i < size1; i++) {
            ints1[i] = 50;
        }
        for (int i = 0; i < size2; i++) {
            ints2[i] = 50;
        }

        for (int i = 0; i < 1000; i++) {
            int[] ints1Copy = new int[size1];
            System.arraycopy(ints1, 0, ints1Copy, 0, ints1.length);
        }
        long time = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            int[] ints1Copy = new int[size1];
            System.arraycopy(ints1, 0, ints1Copy, 0, ints1.length);
        }
        long duration = System.currentTimeMillis() - time;
        System.out.println("ARRAY OF FIRST SIZE FIRST RUN:");
        System.out.println("TIME: " + duration + " ms");

        for (int i = 0; i < 1000; i++) {
            int[] ints1Copy = new int[size1];
            System.arraycopy(ints1, 0, ints1Copy, 0, ints1.length);
        }
        time = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            int[] ints1Copy = new int[size1];
            System.arraycopy(ints1, 0, ints1Copy, 0, ints1.length);
        }
        duration = System.currentTimeMillis() - time;
        System.out.println("-------------");
        System.out.println("ARRAY OF FIRST SIZE SECOND RUN:");
        System.out.println("TIME: " + duration + " ms");

        for (int i = 0; i < 1000; i++) {
            int[] ints2Copy = new int[size2];
            System.arraycopy(ints2, 0, ints2Copy, 0, ints2.length);
        }
        time = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            int[] ints2Copy = new int[size2];
            System.arraycopy(ints2, 0, ints2Copy, 0, ints2.length);
        }
        duration = System.currentTimeMillis() - time;
        System.out.println("-------------");
        System.out.println("ARRAY OF SECOND SIZE FIRST RUN:");
        System.out.println("TIME: " + duration + " ms");
    }

    private static void testOneArrayVsTwo() {
        int[] ints60 = new int[60];
        int[] ints50 = new int[50];
        int[] ints10 = new int[10];
        for (int i = 0; i < 60; i++) {
            ints60[i] = 50;
        }
        for (int i = 0; i < 50; i++) {
            ints50[i] = 50;
        }
        for (int i = 0; i < 10; i++) {
            ints10[i] = 50;
        }

        for (int i = 0; i < 1000; i++) {
            int[] ints60Copy = new int[60];
            System.arraycopy(ints60, 0, ints60Copy, 0, ints60.length);
        }
        long time = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            int[] ints60Copy = new int[60];
            System.arraycopy(ints60, 0, ints60Copy, 0, ints60.length);
        }
        long duration = System.currentTimeMillis() - time;
        System.out.println("ONE ARRAY:");
        System.out.println("TIME: " + duration + " ms");

        for (int i = 0; i < 1000; i++) {
            int[] ints50Copy = new int[50];
            int[] ints10Copy = new int[10];
            System.arraycopy(ints50, 0, ints50Copy, 0, ints50.length);
            System.arraycopy(ints10, 0, ints10Copy, 0, ints10.length);
        }
        time = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            int[] ints50Copy = new int[50];
            int[] ints10Copy = new int[10];
            System.arraycopy(ints50, 0, ints50Copy, 0, ints50.length);
            System.arraycopy(ints10, 0, ints10Copy, 0, ints10.length);
        }
        duration = System.currentTimeMillis() - time;
        System.out.println("-------------");
        System.out.println("TWO ARRAYS:");
        System.out.println("TIME: " + duration + " ms");
    }

    private static void testOneArrayVsFour() {
        int[] ints400 = new int[400];
        int[] ints100_1 = new int[100];
        int[] ints100_2 = new int[100];
        int[] ints100_3 = new int[100];
        int[] ints100_4 = new int[100];
        for (int i = 0; i < 400; i++) {
            ints400[i] = 50;
        }
        for (int i = 0; i < 100; i++) {
            ints100_1[i] = 40;
            ints100_2[i] = 30;
            ints100_3[i] = 20;
            ints100_4[i] = 10;
        }

        for (int i = 0; i < 1000; i++) {
            int[] ints400Copy = new int[400];
            //System.arraycopy(ints400, 0, ints400Copy, 0, ints400.length);
        }
        long time = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            int[] ints400Copy = new int[400];
            //System.arraycopy(ints400, 0, ints400Copy, 0, ints400.length);
        }
        long duration = System.currentTimeMillis() - time;
        System.out.println("ONE ARRAY:");
        System.out.println("TIME: " + duration + " ms");

        for (int i = 0; i < 1000; i++) {
            int[] ints100_1_Copy = new int[100];
            int[] ints100_2_Copy = new int[100];
            int[] ints100_3_Copy = new int[100];
            int[] ints100_4_Copy = new int[100];
            /*
            System.arraycopy(ints100_1, 0, ints100_1_Copy, 0, ints100_1.length);
            System.arraycopy(ints100_2, 0, ints100_2_Copy, 0, ints100_2.length);
            System.arraycopy(ints100_3, 0, ints100_3_Copy, 0, ints100_3.length);
            System.arraycopy(ints100_4, 0, ints100_4_Copy, 0, ints100_4.length);
            */
        }
        time = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            int[] ints100_1_Copy = new int[100];
            int[] ints100_2_Copy = new int[100];
            int[] ints100_3_Copy = new int[100];
            int[] ints100_4_Copy = new int[100];
            /*
            System.arraycopy(ints100_1, 0, ints100_1_Copy, 0, ints100_1.length);
            System.arraycopy(ints100_2, 0, ints100_2_Copy, 0, ints100_2.length);
            System.arraycopy(ints100_3, 0, ints100_3_Copy, 0, ints100_3.length);
            System.arraycopy(ints100_4, 0, ints100_4_Copy, 0, ints100_4.length);
            */
        }
        duration = System.currentTimeMillis() - time;
        System.out.println("--------------");
        System.out.println("FOUR ARRAYS:");
        System.out.println("TIME: " + duration + " ms");
    }

    private static void testArraysCreation() {
        long time = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            int[] ints = new int[400];
        }
        System.out.println("Million array 400 inits: " + (System.currentTimeMillis() - time));

        time = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            int[] ints1 = new int[100];
            int[] ints2 = new int[100];
            int[] ints3 = new int[100];
            int[] ints4 = new int[100];
        }
        System.out.println("Million 4x array 100 inits: " + (System.currentTimeMillis() - time));

        int[] ints = new int[400];
        int[] intsCopy = new int[400];
        time = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            System.arraycopy(ints, 0, intsCopy, 0, ints.length);
        }
        System.out.println("Million array copies: " + (System.currentTimeMillis() - time));
    }

    private static void testClones() {
        String level = getLevel("./levels/original/lvl-1.txt");
        MarioWorld setupWorld = new MarioWorld(null);
        setupWorld.visuals = false;
        setupWorld.initializeLevel(level, 1000 * 200);
        setupWorld.mario.isLarge = false;
        setupWorld.mario.isFire = false;
        setupWorld.update(new boolean[MarioActions.numberOfActions()]);

        MarioForwardModel originalModel = new MarioForwardModel(setupWorld.clone());
        MarioForwardModelSlim slimModel = Converter.originalToSlim(originalModel, 27);
        MarioForwardModelSlim slimModelWindow = Converter.originalToSlim(originalModel, 27);
        MarioForwardModelBin binModel = Converter.slimToBin(slimModel);

        for (int i = 0; i < 100000; i++) {
            originalModel.clone();
        }
        long time = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            originalModel.clone();
        }
        long duration = System.currentTimeMillis() - time;
        System.out.println("ORIGINAL MODEL");
        System.out.println("TIME: " + duration + " ms");
        System.out.print("Clones per second: ");
        System.out.print(String.format("%,.0f", 1000000 / (duration / 1000.0)));
        System.out.println(" clones");

        for (int i = 0; i < 1000000; i++) {
            slimModel.clone();
        }
        time = System.currentTimeMillis();
        for (int i = 0; i < 10000000; i++) {
            slimModel.clone();
        }
        duration = System.currentTimeMillis() - time;
        System.out.println("--------------");
        System.out.println("SLIM MODEL");
        System.out.println("TIME: " + duration + " ms");
        System.out.print("Clones per second: ");
        System.out.print(String.format("%,.0f", 10000000 / (duration / 1000.0)));
        System.out.println(" clones");

        for (int i = 0; i < 1000000; i++) {
            slimModelWindow.clone();
        }
        time = System.currentTimeMillis();
        for (int i = 0; i < 10000000; i++) {
            slimModelWindow.clone();
        }
        duration = System.currentTimeMillis() - time;
        System.out.println("--------------");
        System.out.println("SLIM WINDOW MODEL");
        System.out.println("TIME: " + duration + " ms");
        System.out.print("Clones per second: ");
        System.out.print(String.format("%,.0f", 10000000 / (duration / 1000.0)));
        System.out.println(" clones");

        for (int i = 0; i < 1000; i++) {
            binModel.clone();
        }
        time = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            binModel.clone();
        }
        duration = System.currentTimeMillis() - time;
        System.out.println("--------------");
        System.out.println("BIN MODEL");
        System.out.println("TIME: " + duration + " ms");
        System.out.print("Clones per second: ");
        System.out.print(String.format("%,.0f", 1000000 / (duration / 1000.0)));
        System.out.println(" clones");

        // pooling test
        /*
        time = System.currentTimeMillis();
        for (int j = 0; j < 10; j++) {
            LinkedList<MarioForwardModelBin> binModelsList = new LinkedList<>();
            for (int i = 0; i < 100000; i++) {
                binModelsList.add(binModel.clone());
            }
            for (MarioForwardModelBin binModelFromList : binModelsList) {
                binModelFromList.returnArrays();
            }
        }
        duration = System.currentTimeMillis() - time;
        System.out.println("--------------");
        System.out.println("BIN MODEL - ARRAY POOLING");
        System.out.println("TIME: " + duration + " ms");
        System.out.print("Clones per second: ");
        System.out.print(String.format("%,.0f", 1000000 / (duration / 1000.0)));
        System.out.println(" clones");
        */
    }

    private static void testArrayCopies() {
        unsafe = getUnsafe();
        LevelPart[] levelParts = new LevelPart[400];
        int[] ints = new int[400];
        byte[] bytes = new byte[400];
        boolean[] bools = new boolean[400];
        float[] floats = new float[400];
        short[] shorts = new short[400];
        for (int i = 0; i < 400; i++) {
            levelParts[i] = LevelPart.PIPE_TOP_LEFT_WITHOUT_FLOWER;
            ints[i] = 50;
            bytes[i] = 50;
            bools[i] = true;
            floats[i] = 1.654168541f;
            shorts[i] = 50;
        }
        LevelPart[] levelPartsCopy = new LevelPart[400];
        int[] intsCopy = new int[400];
        byte[] bytesCopy = new byte[400];
        boolean[] boolsCopy = new boolean[400];
        float[] floatsCopy = new float[400];
        short[] shortsCopy = new short[400];

        // bools, arraycopy
        for (int i = 0; i < 10000; i++) {
            System.arraycopy(bools, 0, boolsCopy, 0, bools.length);
        }
        long start = System.currentTimeMillis();
        for (int i = 0; i < 10000000; i++) {
            System.arraycopy(bools, 0, boolsCopy, 0, bools.length);
        }
        long end = System.currentTimeMillis();
        long time = end - start;
        System.out.println("bools - arraycopy: " + time);

        // bytes, arraycopy
        for (int i = 0; i < 10000; i++) {
            System.arraycopy(bytes, 0, bytesCopy, 0, bytes.length);
        }
        start = System.currentTimeMillis();
        for (int i = 0; i < 10000000; i++) {
            System.arraycopy(bytes, 0, bytesCopy, 0, bytes.length);
        }
        end = System.currentTimeMillis();
        time = end - start;
        System.out.println("bytes - arraycopy: " + time);

        // shorts, arraycopy
        for (int i = 0; i < 10000; i++) {
            System.arraycopy(shorts, 0, shortsCopy, 0, shorts.length);
        }
        start = System.currentTimeMillis();
        for (int i = 0; i < 10000000; i++) {
            System.arraycopy(shorts, 0, shortsCopy, 0, shorts.length);
        }
        end = System.currentTimeMillis();
        time = end - start;
        System.out.println("shorts - arraycopy: " + time);

        // ints, arraycopy
        for (int i = 0; i < 10000; i++) {
            System.arraycopy(ints, 0, intsCopy, 0, ints.length);
        }
        start = System.currentTimeMillis();
        for (int i = 0; i < 10000000; i++) {
            System.arraycopy(ints, 0, intsCopy, 0, ints.length);
        }
        end = System.currentTimeMillis();
        time = end - start;
        System.out.println("ints - arraycopy: " + time);

        // level parts, arraycopy
        for (int i = 0; i < 10000; i++) {
            System.arraycopy(levelParts, 0, levelPartsCopy, 0, levelParts.length);
        }
        start = System.currentTimeMillis();
        for (int i = 0; i < 10000000; i++) {
            System.arraycopy(levelParts, 0, levelPartsCopy, 0, levelParts.length);
        }
        end = System.currentTimeMillis();
        time = end - start;
        System.out.println("levelParts - arraycopy: " + time);

        // floats, arraycopy
        for (int i = 0; i < 10000; i++) {
            System.arraycopy(floats, 0, floatsCopy, 0, floats.length);
        }
        start = System.currentTimeMillis();
        for (int i = 0; i < 10000000; i++) {
            System.arraycopy(floats, 0, floatsCopy, 0, floats.length);
        }
        end = System.currentTimeMillis();
        time = end - start;
        System.out.println("floats - arraycopy: " + time);

        System.out.println("---------------");

        // bool, copyMemory
        for (int i = 0; i < 10000; i++) {
            unsafe.copyMemory(bools, 0, boolsCopy, 0, bools.length);
        }
        start = System.currentTimeMillis();
        for (int i = 0; i < 10000000; i++) {
            unsafe.copyMemory(bools, 0, boolsCopy, 0, bools.length);
        }
        end = System.currentTimeMillis();
        time = end - start;
        System.out.println("bools - copyMemory: " + time);

        // bytes, copyMemory
        for (int i = 0; i < 10000; i++) {
            unsafe.copyMemory(bytes, 0, bytesCopy, 0, bytes.length);
        }
        start = System.currentTimeMillis();
        for (int i = 0; i < 10000000; i++) {
            unsafe.copyMemory(bytes, 0, bytesCopy, 0, bytes.length);
        }
        end = System.currentTimeMillis();
        time = end - start;
        System.out.println("bytes - copyMemory: " + time);

        // shorts, copyMemory
        for (int i = 0; i < 10000; i++) {
            unsafe.copyMemory(shorts, 0, shortsCopy, 0, shorts.length);
        }
        start = System.currentTimeMillis();
        for (int i = 0; i < 10000000; i++) {
            unsafe.copyMemory(shorts, 0, shortsCopy, 0, shorts.length);
        }
        end = System.currentTimeMillis();
        time = end - start;
        System.out.println("shorts - copyMemory: " + time);

        // ints, copyMemory
        for (int i = 0; i < 10000; i++) {
            unsafe.copyMemory(ints, 0, intsCopy, 0, (ints.length + 4) * 4);
        }
        start = System.currentTimeMillis();
        for (int i = 0; i < 10000000; i++) {
            unsafe.copyMemory(ints, 0, intsCopy, 0, (ints.length + 4) * 4);
        }
        end = System.currentTimeMillis();
        time = end - start;
        System.out.println("ints - copyMemory: " + time);

        // floats, copyMemory
        for (int i = 0; i < 10000; i++) {
            unsafe.copyMemory(floats, 0, floatsCopy, 0, (floats.length + 4) * 4);
        }
        start = System.currentTimeMillis();
        for (int i = 0; i < 10000000; i++) {
            unsafe.copyMemory(floats, 0, floatsCopy, 0, (floats.length + 4) * 4);
        }
        end = System.currentTimeMillis();
        time = end - start;
        System.out.println("floats - copyMemory: " + time);

/*      // seems like only primitive types can be copied like this
        // level parts, copyMemory
        for (int i = 0; i < 10000; i++) {
            //unsafe.copyMemory(levelParts, 0, levelPartsCopy, 0, levelParts.length * 4);
        }
        long start = System.currentTimeMillis();
        for (int i = 0; i < 10000000; i++) {
            //unsafe.copyMemory(levelParts, 0, levelPartsCopy, 0, levelParts.length * 4);
        }
        long end = System.currentTimeMillis();
        long time = end - start;
        System.out.println("levelParts: " + time);
*/

        System.out.println("---------------");

        // bools, arrays.copyof
        for (int i = 0; i < 10000; i++) {
            boolsCopy = Arrays.copyOf(bools, bools.length);
        }
        start = System.currentTimeMillis();
        for (int i = 0; i < 10000000; i++) {
            boolsCopy = Arrays.copyOf(bools, bools.length);
        }
        end = System.currentTimeMillis();
        time = end - start;
        System.out.println("bools - arrays.copyof: " + time);

        // bytes, arrays.copyof
        for (int i = 0; i < 10000; i++) {
            bytesCopy = Arrays.copyOf(bytes, bytes.length);
        }
        start = System.currentTimeMillis();
        for (int i = 0; i < 10000000; i++) {
            bytesCopy = Arrays.copyOf(bytes, bytes.length);
        }
        end = System.currentTimeMillis();
        time = end - start;
        System.out.println("bytes - arrays.copyof: " + time);

        // shorts, arrays.copyof
        for (int i = 0; i < 10000; i++) {
            shortsCopy = Arrays.copyOf(shorts, shorts.length);
        }
        start = System.currentTimeMillis();
        for (int i = 0; i < 10000000; i++) {
            shortsCopy = Arrays.copyOf(shorts, shorts.length);
        }
        end = System.currentTimeMillis();
        time = end - start;
        System.out.println("shorts - arrays.copyof: " + time);

        // ints, arrays.copyof
        for (int i = 0; i < 10000; i++) {
            intsCopy = Arrays.copyOf(ints, ints.length);
        }
        start = System.currentTimeMillis();
        for (int i = 0; i < 10000000; i++) {
            intsCopy = Arrays.copyOf(ints, ints.length);
        }
        end = System.currentTimeMillis();
        time = end - start;
        System.out.println("ints - arrays.copyof: " + time);

        // level parts, arrays.copyof
        for (int i = 0; i < 10000; i++) {
            levelPartsCopy = Arrays.copyOf(levelParts, levelParts.length);
        }
        start = System.currentTimeMillis();
        for (int i = 0; i < 10000000; i++) {
            levelPartsCopy = Arrays.copyOf(levelParts, levelParts.length);
        }
        end = System.currentTimeMillis();
        time = end - start;
        System.out.println("levelParts - arrays.copyof: " + time);

        // floats, arrays.copyof
        for (int i = 0; i < 10000; i++) {
            floatsCopy = Arrays.copyOf(floats, floats.length);
        }
        start = System.currentTimeMillis();
        for (int i = 0; i < 10000000; i++) {
            floatsCopy = Arrays.copyOf(floats, floats.length);
        }
        end = System.currentTimeMillis();
        time = end - start;
        System.out.println("floats - arrays.copyof: " + time);

        // trying to prevent optimization
        ints[0] = intsCopy[0];
        levelParts[0] = levelPartsCopy[0];
        bytes[0] = bytesCopy[0];
        bools[0] = boolsCopy[0];
        floats[0] = floatsCopy[0];
        shorts[0] = shortsCopy[0];
    }
}
