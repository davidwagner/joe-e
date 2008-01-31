package test.library.array;

import org.joe_e.array.*;
import java.util.Arrays;

public class Builders {
    public static void test() {
       /*
        * ByteArray.BuilderOutputStream
        */
       ByteArray.BuilderOutputStream babosn = 
           new ByteArray.BuilderOutputStream(-23);
       ByteArray.BuilderOutputStream babos0 = 
           new ByteArray.BuilderOutputStream(0);
       ByteArray.BuilderOutputStream babos1 = 
           new ByteArray.BuilderOutputStream(1);
       ByteArray.BuilderOutputStream babos1000 = 
           new ByteArray.BuilderOutputStream(1000);
       
       int[] ints1 = {1, 2, 3, 4, 5, -12, 34, 100, 1000, 10000, -1000, -10000};
       byte[] bytes = {1, 2, 3, 4, 5, -12, 34, 127};
       int[][] ranges = {{0, 8}, {1, 0}, {3, 5}, {7, 1}};

       for (int i : ints1) {
           babosn.write(i);
           babos0.write(i);
           babos1.write(i);
           babos1000.write(i);
       }
       
       byte[] correct1 = {1, 2, 3, 4, 5, -12, 34, 100, (byte) 1000,
                          (byte) 10000, (byte) -1000, (byte) -10000}; 
       assert Arrays.equals(babosn.snapshot().toByteArray(), correct1);
       assert Arrays.equals(babos0.snapshot().toByteArray(), correct1);
       assert Arrays.equals(babos1.snapshot().toByteArray(), correct1);
       assert Arrays.equals(babos1000.snapshot().toByteArray(), correct1);
              
       babosn.write(bytes);
       babos0.write(bytes);
       babos1.write(bytes);
       babos1000.write(bytes);
       
       byte[] correct2 = {1, 2, 3, 4, 5, -12, 34, 100, (byte) 1000,
                          (byte) 10000, (byte) -1000, (byte) -10000,
                          1, 2, 3, 4, 5, -12, 34, 127}; 
       assert Arrays.equals(babosn.snapshot().toByteArray(), correct2);
       assert Arrays.equals(babos0.snapshot().toByteArray(), correct2);
       assert Arrays.equals(babos1.snapshot().toByteArray(), correct2);
       assert Arrays.equals(babos1000.snapshot().toByteArray(), correct2);
       
       for (int[] r : ranges) {           
           babosn.write(bytes, r[0], r[1]);
           babos0.write(bytes, r[0], r[1]);
           babos1.write(bytes, r[0], r[1]);
           babos1000.write(bytes, r[0], r[1]);
       }

       byte[] correct3 = {1, 2, 3, 4, 5, -12, 34, 100, (byte) 1000,
               (byte) 10000, (byte) -1000, (byte) -10000,
               1, 2, 3, 4, 5, -12, 34, 127,
               1, 2, 3, 4, 5, -12, 34, 127,
               4, 5, -12, 34, 127, 127}; 

       assert Arrays.equals(babosn.snapshot().toByteArray(), correct3);
       assert Arrays.equals(babos0.snapshot().toByteArray(), correct3);
       assert Arrays.equals(babos1.snapshot().toByteArray(), correct3);
       assert Arrays.equals(babos1000.snapshot().toByteArray(), correct3);

       // test append(byte[]) from scratch
       babosn = new ByteArray.BuilderOutputStream(-23);
       babos0 = new ByteArray.BuilderOutputStream(0);
       babos1 = new ByteArray.BuilderOutputStream(1);
       babos1000 = new ByteArray.BuilderOutputStream(1000);
       
       babosn.write(bytes);
       babos0.write(bytes);
       babos1.write(bytes);
       babos1000.write(bytes);
       
       byte[] correct4 = {1, 2, 3, 4, 5, -12, 34, 127}; 

       assert Arrays.equals(babosn.snapshot().toByteArray(), correct4);
       assert Arrays.equals(babos0.snapshot().toByteArray(), correct4);
       assert Arrays.equals(babos1.snapshot().toByteArray(), correct4);
       assert Arrays.equals(babos1000.snapshot().toByteArray(), correct4);

       for (int i : ints1) {
           babosn.write(i);
           babos0.write(i);
           babos1.write(i);
           babos1000.write(i);
       }
       
       byte[] correct5 = {1, 2, 3, 4, 5, -12, 34, 127,
                          1, 2, 3, 4, 5, -12, 34, 100, (byte) 1000,
                          (byte) 10000, (byte) -1000, (byte) -10000};      
       
       assert Arrays.equals(babosn.snapshot().toByteArray(), correct5);
       assert Arrays.equals(babos0.snapshot().toByteArray(), correct5);
       assert Arrays.equals(babos1.snapshot().toByteArray(), correct5);
       assert Arrays.equals(babos1000.snapshot().toByteArray(), correct5);
                     
       // test append(byte[], int, int) from scratch
       babosn = new ByteArray.BuilderOutputStream(-23);
       babos0 = new ByteArray.BuilderOutputStream(0);
       babos1 = new ByteArray.BuilderOutputStream(1);
       babos1000 = new ByteArray.BuilderOutputStream(1000);
       
       for (int[] r : ranges) {           
           babosn.write(bytes, r[0], r[1]);
           babos0.write(bytes, r[0], r[1]);
           babos1.write(bytes, r[0], r[1]);
           babos1000.write(bytes, r[0], r[1]);
       }
       
       byte[] correct6 = {1, 2, 3, 4, 5, -12, 34, 127,
                          4, 5, -12, 34, 127, 127}; 
       
       assert Arrays.equals(babosn.snapshot().toByteArray(), correct6);
       assert Arrays.equals(babos0.snapshot().toByteArray(), correct6);
       assert Arrays.equals(babos1.snapshot().toByteArray(), correct6);
       assert Arrays.equals(babos1000.snapshot().toByteArray(), correct6);
     
       // exception when length < 0
       try {
           babosn.write(bytes, 0, -23);
           assert false;
       } catch (IndexOutOfBoundsException iobe) {}
       
       // ByteArray.asInputStream()
       ByteArray.Builder babNew = new ByteArray.Builder();
       ByteArray someBytes = babosn.snapshot();
       java.io.InputStream byteStream = someBytes.asInputStream();
       try {
           byte[] buffer = new byte[4];
           int bytesRead = byteStream.read(buffer);
           while (bytesRead > 0) {
               babNew.append(buffer, 0, bytesRead);
               bytesRead = byteStream.read(buffer);
           }
       } catch (java.io.IOException ioe) {
           assert false;
       }
       
       assert (babNew.snapshot().equals(someBytes));
 
       /*
        * ByteArray.Builder
        */
       ByteArray.Builder babn = new ByteArray.Builder(-23);
       ByteArray.Builder bab0 = new ByteArray.Builder(0);
       ByteArray.Builder bab1 = new ByteArray.Builder(1);
       ByteArray.Builder bab1000 = new ByteArray.Builder(1000);
       
       byte[] bytes1 = {1, 2, 3, 4, 5, -12, 34, 100, (byte) 1000, (byte) 10000, (byte) -1000, (byte) -10000};
       Byte[] wrappedBytes = {1, 2, 3, 4, 5, -12, 34, 127};
       
       for (byte b : bytes1) {
           babn.append((Byte) b);
           bab0.append(b);
           bab1.append((Byte) b);
           bab1000.append(b);
       }
       
       assert Arrays.equals(babn.snapshot().toByteArray(), correct1);
       assert Arrays.equals(bab0.snapshot().toByteArray(), correct1);
       assert Arrays.equals(bab1.snapshot().toByteArray(), correct1);
       assert Arrays.equals(bab1000.snapshot().toByteArray(), correct1);
                    
       babn.append(bytes);
       bab0.append(wrappedBytes);
       bab1.append(bytes);
       bab1000.append(wrappedBytes);
       
       assert Arrays.equals(babn.snapshot().toByteArray(), correct2);
       assert Arrays.equals(bab0.snapshot().toByteArray(), correct2);
       assert Arrays.equals(bab1.snapshot().toByteArray(), correct2);
       assert Arrays.equals(bab1000.snapshot().toByteArray(), correct2);
       
       for (int[] r : ranges) {           
           babn.append(wrappedBytes, r[0], r[1]);
           bab0.append(bytes, r[0], r[1]);
           bab1.append(wrappedBytes, r[0], r[1]);
           bab1000.append(bytes, r[0], r[1]);
       }

       assert Arrays.equals(babn.snapshot().toByteArray(), correct3);
       assert Arrays.equals(bab0.snapshot().toByteArray(), correct3);
       assert Arrays.equals(bab1.snapshot().toByteArray(), correct3);
       assert Arrays.equals(bab1000.snapshot().toByteArray(), correct3);

       // test append(byte[]) from scratch
       babn = new ByteArray.Builder(-23);
       bab0 = new ByteArray.Builder(0);
       bab1 = new ByteArray.Builder(1);
       bab1000 = new ByteArray.Builder(1000);
       
       babn.append(bytes);
       bab0.append(wrappedBytes);
       bab1.append(bytes);
       bab1000.append(wrappedBytes);

       assert Arrays.equals(babn.snapshot().toByteArray(), correct4);
       assert Arrays.equals(bab0.snapshot().toByteArray(), correct4);
       assert Arrays.equals(bab1.snapshot().toByteArray(), correct4);
       assert Arrays.equals(bab1000.snapshot().toByteArray(), correct4);

       for (byte b : bytes1) {
           babn.append(b);
           bab0.append((Byte) b);
           bab1.append((Byte) b);
           bab1000.append(b);
       }
       
       assert Arrays.equals(babn.snapshot().toByteArray(), correct5);
       assert Arrays.equals(bab0.snapshot().toByteArray(), correct5);
       assert Arrays.equals(bab1.snapshot().toByteArray(), correct5);
       assert Arrays.equals(bab1000.snapshot().toByteArray(), correct5);
                     
       // test append(byte[], int, int) from scratch
       babn = new ByteArray.Builder(-23);
       bab0 = new ByteArray.Builder(0);
       bab1 = new ByteArray.Builder(1);
       bab1000 = new ByteArray.Builder(1000);
       
       for (int[] r : ranges) {           
           babn.append(wrappedBytes, r[0], r[1]);
           bab0.append(bytes, r[0], r[1]);
           bab1.append(bytes, r[0], r[1]);
           bab1000.append(wrappedBytes, r[0], r[1]);
       }
       
       assert Arrays.equals(babn.snapshot().toByteArray(), correct6);
       assert Arrays.equals(bab0.snapshot().toByteArray(), correct6);
       assert Arrays.equals(bab1.snapshot().toByteArray(), correct6);
       assert Arrays.equals(bab1000.snapshot().toByteArray(), correct6);
     
       // exception when length < 0
       try {
           babn.append(bytes, 0, -23);
           assert false;
       } catch (IndexOutOfBoundsException iobe) {}

       try {
           babn.append(wrappedBytes, 0, -23);
           assert false;
       } catch (IndexOutOfBoundsException iobe) {}

       /*
        * CharArray.Builder
        */
       CharArray.Builder cabn = new CharArray.Builder(-23);
       CharArray.Builder cab0 = new CharArray.Builder(0);
       CharArray.Builder cab1 = new CharArray.Builder(1);
       CharArray.Builder cab1000 = new CharArray.Builder(1000);
       
       Character[] characters1 = {'a', 'b', 'A', 'B', '*', '`', 'õ', 'ß'};
       char[] chars1 = {'a', 'b', 'A', 'B', '*', '`', 'õ', 'ß'};
       
       for (Character ch : characters1) {
           cabn.append(ch);
           cab0.append(ch);
           cab1.append(ch);
           cab1000.append(ch);
       }
             
       assert Arrays.equals(cabn.snapshot().toCharArray(), chars1);
       assert Arrays.equals(cab0.snapshot().toCharArray(), chars1);
       assert Arrays.equals(cab1.snapshot().toCharArray(), chars1);
       assert Arrays.equals(cab1000.snapshot().toCharArray(), chars1);
              
       cabn.append(characters1);
       cab0.append(chars1);
       cab1.append(characters1);
       cab1000.append(chars1);

       char[] correctChars2 = {'a', 'b', 'A', 'B', '*', '`', 'õ', 'ß',
                               'a', 'b', 'A', 'B', '*', '`', 'õ', 'ß'};

       assert Arrays.equals(cabn.snapshot().toCharArray(), correctChars2);
       assert Arrays.equals(cab0.snapshot().toCharArray(), correctChars2);
       assert Arrays.equals(cab1.snapshot().toCharArray(), correctChars2);
       assert Arrays.equals(cab1000.snapshot().toCharArray(), correctChars2);
       
       for (int[] r : ranges) {           
           cabn.append(chars1, r[0], r[1]);
           cab0.append(characters1, r[0], r[1]);
           cab1.append(chars1, r[0], r[1]);
           cab1000.append(characters1, r[0], r[1]);
       }

       char[] correctChars3 = {'a', 'b', 'A', 'B', '*', '`', 'õ', 'ß',
                               'a', 'b', 'A', 'B', '*', '`', 'õ', 'ß',
                               'a', 'b', 'A', 'B', '*', '`', 'õ', 'ß',
                               'B', '*', '`', 'õ', 'ß', 'ß'};

       assert Arrays.equals(cabn.snapshot().toCharArray(), correctChars3);
       assert Arrays.equals(cab0.snapshot().toCharArray(), correctChars3);
       assert Arrays.equals(cab1.snapshot().toCharArray(), correctChars3);
       assert Arrays.equals(cab1000.snapshot().toCharArray(), correctChars3);

       // test append(char[]) from scratch
       cabn = new CharArray.Builder(-23);
       cab0 = new CharArray.Builder(0);
       cab1 = new CharArray.Builder(1);
       cab1000 = new CharArray.Builder(1000);
       
       cabn.append(chars1);
       cab0.append(characters1);
       cab1.append(chars1);
       cab1000.append(characters1);

       assert Arrays.equals(cabn.snapshot().toCharArray(), chars1);
       assert Arrays.equals(cab0.snapshot().toCharArray(), chars1);
       assert Arrays.equals(cab1.snapshot().toCharArray(), chars1);
       assert Arrays.equals(cab1000.snapshot().toCharArray(), chars1);

       for (char ch : chars1) {
           cabn.append(ch);
           cab0.append(ch);
           cab1.append(ch);
           cab1000.append(ch);
       }

       assert Arrays.equals(cabn.snapshot().toCharArray(), correctChars2);
       assert Arrays.equals(cab0.snapshot().toCharArray(), correctChars2);
       assert Arrays.equals(cab1.snapshot().toCharArray(), correctChars2);
       assert Arrays.equals(cab1000.snapshot().toCharArray(), correctChars2);
                     
       // test append(char[], int, int) from scratch
       cabn = new CharArray.Builder(-23);
       cab0 = new CharArray.Builder(0);
       cab1 = new CharArray.Builder(1);
       cab1000 = new CharArray.Builder(1000);
       
       for (int[] r : ranges) {           
           cabn.append(characters1, r[0], r[1]);
           cab0.append(chars1, r[0], r[1]);
           cab1.append(characters1, r[0], r[1]);
           cab1000.append(chars1, r[0], r[1]);
       }
       
       char[] correctChars6 = {'a', 'b', 'A', 'B', '*', '`', 'õ', 'ß',
                              'B', '*', '`', 'õ', 'ß', 'ß'}; 
       
       assert Arrays.equals(cabn.snapshot().toCharArray(), correctChars6);
       assert Arrays.equals(cab0.snapshot().toCharArray(), correctChars6);
       assert Arrays.equals(cab1.snapshot().toCharArray(), correctChars6);
       assert Arrays.equals(cab1000.snapshot().toCharArray(), correctChars6);
     
       // exception when length < 0
       try {
           cabn.append(chars1, 0, -23);
           assert false;
       } catch (IndexOutOfBoundsException iobe) {}
       
       try {
           cab0.append(characters1, 0, -23);
           assert false;
       } catch (IndexOutOfBoundsException iobe) {}
       
       /*
        * ConstArray
        */
       ConstArray.Builder<Integer> cabin = new ConstArray.Builder<Integer>(-23);
       ConstArray.Builder<Integer> cabi0 = new ConstArray.Builder<Integer>(0);
       ConstArray.Builder<Integer> cabi1 = new ConstArray.Builder<Integer>(1);
       ConstArray.Builder<Integer> cabi1000 = new ConstArray.Builder<Integer>(1000);
       
       int[] ints2 = {1, 2, -12, -34, 100, 10000, -1000, -10000};
       Integer[] integers2 = {1, 2, -12, -34, 100, 10000, -1000, -10000};
       
       for (int i : ints2) {
           cabin.append(i);
           cabi0.append(i);
           cabi1.append(i);
           cabi1000.append(i);
       }
             
       assert Arrays.equals(cabin.snapshot().toArray(new Object[]{}), 
                            integers2);
       assert Arrays.equals(cabi0.snapshot().toArray(new Integer[]{}), 
                            integers2);
       assert Arrays.equals(cabi1.snapshot().toArray(new Object[]{}), 
                            integers2);
       assert Arrays.equals(cabi1000.snapshot().toArray(new Number[]{}), 
                            integers2);
              
       cabin.append(integers2);
       cabi0.append(integers2);
       cabi1.append(integers2);
       cabi1000.append(integers2);

       Integer[] correctIntegers2 = {1, 2, -12, -34, 100, 10000, -1000, -10000,
                                     1, 2, -12, -34, 100, 10000, -1000, -10000};

       assert Arrays.equals(cabin.snapshot().toArray(new Number[]{}), 
                            correctIntegers2);
       assert Arrays.equals(cabi0.snapshot().toArray(new Object[]{}), 
                            correctIntegers2);
       assert Arrays.equals(cabi1.snapshot().toArray(new Integer[]{}), 
                            correctIntegers2);
       assert Arrays.equals(cabi1000.snapshot().toArray(new Object[]{}), 
                            correctIntegers2);
       
       for (int[] r : ranges) {           
           cabin.append(integers2, r[0], r[1]);
           cabi0.append(integers2, r[0], r[1]);
           cabi1.append(integers2, r[0], r[1]);
           cabi1000.append(integers2, r[0], r[1]);
       }

       Integer[] correctIntegers3 = {1, 2, -12, -34, 100, 10000, -1000, -10000,
                                     1, 2, -12, -34, 100, 10000, -1000, -10000,
                                     1, 2, -12, -34, 100, 10000, -1000, -10000,
                                     -34, 100, 10000, -1000, -10000, -10000};

       assert Arrays.equals(cabin.snapshot().toArray(new Object[]{}), 
                            correctIntegers3);
       assert Arrays.equals(cabi0.snapshot().toArray(new Number[]{}), 
                            correctIntegers3);
       assert Arrays.equals(cabi1.snapshot().toArray(new Object[]{}), 
                            correctIntegers3);
       assert Arrays.equals(cabi1000.snapshot().toArray(new Integer[]{}), 
                            correctIntegers3);

       // test append(Integer[]) from scratch
       cabin = new ConstArray.Builder<Integer>(-23);
       cabi0 = new ConstArray.Builder<Integer>(0);
       cabi1 = new ConstArray.Builder<Integer>(1);
       cabi1000 = new ConstArray.Builder<Integer>(1000);
       
       cabin.append(integers2);
       cabi0.append(integers2);
       cabi1.append(integers2);
       cabi1000.append(integers2);

       assert Arrays.equals(cabin.snapshot().toArray(new Integer[]{}),
                            integers2);
       assert Arrays.equals(cabi0.snapshot().toArray(new Object[]{}),
                            integers2);
       assert Arrays.equals(cabi1.snapshot().toArray(new Number[]{}),
                            integers2);
       assert Arrays.equals(cabi1000.snapshot().toArray(new Object[]{}),
                            integers2);

       for (Integer i : ints2) {
           cabin.append(i);
           cabi0.append(i);
           cabi1.append(i);
           cabi1000.append(i);
       }

       assert Arrays.equals(cabin.snapshot().toArray(new Object[]{}), 
                            correctIntegers2);
       assert Arrays.equals(cabi0.snapshot().toArray(new Integer[]{}), 
                            correctIntegers2);
       assert Arrays.equals(cabi1.snapshot().toArray(new Number[]{}), 
                            correctIntegers2);
       assert Arrays.equals(cabi1000.snapshot().toArray(new Object[]{}), 
                            correctIntegers2);
                     
       // test append(char[], int, int) from scratch
       // test append(Integer[]) from scratch
       cabin = new ConstArray.Builder<Integer>(-23);
       cabi0 = new ConstArray.Builder<Integer>(0);
       cabi1 = new ConstArray.Builder<Integer>(1);
       cabi1000 = new ConstArray.Builder<Integer>(1000);
   
       for (int[] r : ranges) {           
           cabin.append(integers2, r[0], r[1]);
           cabi0.append(integers2, r[0], r[1]);
           cabi1.append(integers2, r[0], r[1]);
           cabi1000.append(integers2, r[0], r[1]);
       }
       
       Integer[] correctIntegers6 = {1, 2, -12, -34, 100, 10000, -1000, -10000,
                                     -34, 100, 10000, -1000, -10000, -10000};
       
       assert Arrays.equals(cabin.snapshot().toArray(new Number[]{}),
                            correctIntegers6);
       assert Arrays.equals(cabi0.snapshot().toArray(new Object[]{}), 
                            correctIntegers6);
       assert Arrays.equals(cabi1.snapshot().toArray(new Object[]{}),
                            correctIntegers6);
       assert Arrays.equals(cabi1000.snapshot().toArray(new Integer[]{}), 
                            correctIntegers6);
     
       // exception when length < 0
       try {
           cabin.append(integers2, 0, -23);
           assert false;
       } catch (IndexOutOfBoundsException iobe) {}

       /*
        * ImmutableArray
        * 
        * boring stuff - basics
        */      
       ImmutableArray.Builder<Integer> iabin = new ImmutableArray.Builder<Integer>(-23);
       ImmutableArray.Builder<Integer> iabi0 = new ImmutableArray.Builder<Integer>(0);
       ImmutableArray.Builder<Integer> iabi1 = new ImmutableArray.Builder<Integer>(1);
       ImmutableArray.Builder<Integer> iabi1000 = new ImmutableArray.Builder<Integer>(1000);
             
       for (int i : ints2) {
           iabin.append(i);
           iabi0.append(i);
           iabi1.append(i);
           iabi1000.append(i);
       }
             
       assert Arrays.equals(iabin.snapshot().toArray(new Object[]{}), 
                            integers2);
       assert Arrays.equals(iabi0.snapshot().toArray(new Integer[]{}), 
                            integers2);
       assert Arrays.equals(iabi1.snapshot().toArray(new Object[]{}), 
                            integers2);
       assert Arrays.equals(iabi1000.snapshot().toArray(new Number[]{}), 
                            integers2);
              
       iabin.append(integers2);
       iabi0.append(integers2);
       iabi1.append(integers2);
       iabi1000.append(integers2);

       assert Arrays.equals(iabin.snapshot().toArray(new Number[]{}), 
                            correctIntegers2);
       assert Arrays.equals(iabi0.snapshot().toArray(new Object[]{}), 
                            correctIntegers2);
       assert Arrays.equals(iabi1.snapshot().toArray(new Integer[]{}), 
                            correctIntegers2);
       assert Arrays.equals(iabi1000.snapshot().toArray(new Object[]{}), 
                            correctIntegers2);
       
       for (int[] r : ranges) {           
           iabin.append(integers2, r[0], r[1]);
           iabi0.append(integers2, r[0], r[1]);
           iabi1.append(integers2, r[0], r[1]);
           iabi1000.append(integers2, r[0], r[1]);
       }

       assert Arrays.equals(iabin.snapshot().toArray(new Object[]{}), 
                            correctIntegers3);
       assert Arrays.equals(iabi0.snapshot().toArray(new Number[]{}), 
                            correctIntegers3);
       assert Arrays.equals(iabi1.snapshot().toArray(new Object[]{}), 
                            correctIntegers3);
       assert Arrays.equals(iabi1000.snapshot().toArray(new Integer[]{}), 
                            correctIntegers3);

       // test append(Integer[]) from scratch
       iabin = new ImmutableArray.Builder<Integer>(-23);
       iabi0 = new ImmutableArray.Builder<Integer>(0);
       iabi1 = new ImmutableArray.Builder<Integer>(1);
       iabi1000 = new ImmutableArray.Builder<Integer>(1000);
       
       iabin.append(integers2);
       iabi0.append(integers2);
       iabi1.append(integers2);
       iabi1000.append(integers2);

       assert Arrays.equals(iabin.snapshot().toArray(new Integer[]{}),
                            integers2);
       assert Arrays.equals(iabi0.snapshot().toArray(new Object[]{}),
                            integers2);
       assert Arrays.equals(iabi1.snapshot().toArray(new Number[]{}),
                            integers2);
       assert Arrays.equals(iabi1000.snapshot().toArray(new Object[]{}),
                            integers2);

       for (Integer i : ints2) {
           iabin.append(i);
           iabi0.append(i);
           iabi1.append(i);
           iabi1000.append(i);
       }

       assert Arrays.equals(iabin.snapshot().toArray(new Object[]{}), 
                            correctIntegers2);
       assert Arrays.equals(iabi0.snapshot().toArray(new Integer[]{}), 
                            correctIntegers2);
       assert Arrays.equals(iabi1.snapshot().toArray(new Number[]{}), 
                            correctIntegers2);
       assert Arrays.equals(iabi1000.snapshot().toArray(new Object[]{}), 
                            correctIntegers2);
                     
       // test append(char[], int, int) from scratch
       // test append(Integer[]) from scratch
       iabin = new ImmutableArray.Builder<Integer>(-23);
       iabi0 = new ImmutableArray.Builder<Integer>(0);
       iabi1 = new ImmutableArray.Builder<Integer>(1);
       iabi1000 = new ImmutableArray.Builder<Integer>(1000);
   
       for (int[] r : ranges) {           
           iabin.append(integers2, r[0], r[1]);
           iabi0.append(integers2, r[0], r[1]);
           iabi1.append(integers2, r[0], r[1]);
           iabi1000.append(integers2, r[0], r[1]);
       }
       
       assert Arrays.equals(iabin.snapshot().toArray(new Number[]{}),
                            correctIntegers6);
       assert Arrays.equals(iabi0.snapshot().toArray(new Object[]{}), 
                            correctIntegers6);
       assert Arrays.equals(iabi1.snapshot().toArray(new Object[]{}),
                            correctIntegers6);
       assert Arrays.equals(iabi1000.snapshot().toArray(new Integer[]{}), 
                            correctIntegers6);
     
       // exception when length < 0
       try {
           iabin.append(integers2, 0, -23);
           assert false;
       } catch (IndexOutOfBoundsException iobe) {}

       
       /*
        * Immutableness integrity: don't allow non-immutables
        */
       ImmutableArray.Builder<Object> iabo = new ImmutableArray.Builder<Object>(42);
       
       try {
           iabo.append(new Object());
           assert false;
       } catch (ClassCastException cce) { }
       
       iabo.append(new String());
       iabo.append(new org.joe_e.Token());
       
       try {
           iabo.append(new Object[]{"hello", "good-bye"});
           assert false;
       } catch (ClassCastException cce) { }
       
       iabo.append(new String[]{"hi", "bye"});
       iabo.append(new org.joe_e.Token[]{});
       
       /*
        * PowerlessArray
        */
       PowerlessArray.Builder<Integer> pabin = new PowerlessArray.Builder<Integer>(-23);
       PowerlessArray.Builder<Integer> pabi0 = new PowerlessArray.Builder<Integer>(0);
       PowerlessArray.Builder<Integer> pabi1 = new PowerlessArray.Builder<Integer>(1);
       PowerlessArray.Builder<Integer> pabi1000 = new PowerlessArray.Builder<Integer>(1000);
             
       for (int i : ints2) {
           pabin.append(i);
           pabi0.append(i);
           pabi1.append(i);
           pabi1000.append(i);
       }
             
       assert Arrays.equals(pabin.snapshot().toArray(new Object[]{}), 
                            integers2);
       assert Arrays.equals(pabi0.snapshot().toArray(new Integer[]{}), 
                            integers2);
       assert Arrays.equals(pabi1.snapshot().toArray(new Object[]{}), 
                            integers2);
       assert Arrays.equals(pabi1000.snapshot().toArray(new Number[]{}), 
                            integers2);
              
       pabin.append(integers2);
       pabi0.append(integers2);
       pabi1.append(integers2);
       pabi1000.append(integers2);

       assert Arrays.equals(pabin.snapshot().toArray(new Number[]{}), 
                            correctIntegers2);
       assert Arrays.equals(pabi0.snapshot().toArray(new Object[]{}), 
                            correctIntegers2);
       assert Arrays.equals(pabi1.snapshot().toArray(new Integer[]{}), 
                            correctIntegers2);
       assert Arrays.equals(pabi1000.snapshot().toArray(new Object[]{}), 
                            correctIntegers2);
       
       for (int[] r : ranges) {           
           pabin.append(integers2, r[0], r[1]);
           pabi0.append(integers2, r[0], r[1]);
           pabi1.append(integers2, r[0], r[1]);
           pabi1000.append(integers2, r[0], r[1]);
       }

       assert Arrays.equals(pabin.snapshot().toArray(new Object[]{}), 
                            correctIntegers3);
       assert Arrays.equals(pabi0.snapshot().toArray(new Number[]{}), 
                            correctIntegers3);
       assert Arrays.equals(pabi1.snapshot().toArray(new Object[]{}), 
                            correctIntegers3);
       assert Arrays.equals(pabi1000.snapshot().toArray(new Integer[]{}), 
                            correctIntegers3);

       // test append(Integer[]) from scratch
       pabin = new PowerlessArray.Builder<Integer>(-23);
       pabi0 = new PowerlessArray.Builder<Integer>(0);
       pabi1 = new PowerlessArray.Builder<Integer>(1);
       pabi1000 = new PowerlessArray.Builder<Integer>(1000);
       
       pabin.append(integers2);
       pabi0.append(integers2);
       pabi1.append(integers2);
       pabi1000.append(integers2);

       assert Arrays.equals(pabin.snapshot().toArray(new Integer[]{}),
                            integers2);
       assert Arrays.equals(pabi0.snapshot().toArray(new Object[]{}),
                            integers2);
       assert Arrays.equals(pabi1.snapshot().toArray(new Number[]{}),
                            integers2);
       assert Arrays.equals(pabi1000.snapshot().toArray(new Object[]{}),
                            integers2);

       for (Integer i : ints2) {
           pabin.append(i);
           pabi0.append(i);
           pabi1.append(i);
           pabi1000.append(i);
       }

       assert Arrays.equals(pabin.snapshot().toArray(new Object[]{}), 
                            correctIntegers2);
       assert Arrays.equals(pabi0.snapshot().toArray(new Integer[]{}), 
                            correctIntegers2);
       assert Arrays.equals(pabi1.snapshot().toArray(new Number[]{}), 
                            correctIntegers2);
       assert Arrays.equals(pabi1000.snapshot().toArray(new Object[]{}), 
                            correctIntegers2);
                     
       // test append(char[], int, int) from scratch
       // test append(Integer[]) from scratch
       pabin = new PowerlessArray.Builder<Integer>(-23);
       pabi0 = new PowerlessArray.Builder<Integer>(0);
       pabi1 = new PowerlessArray.Builder<Integer>(1);
       pabi1000 = new PowerlessArray.Builder<Integer>(1000);
   
       for (int[] r : ranges) {           
           pabin.append(integers2, r[0], r[1]);
           pabi0.append(integers2, r[0], r[1]);
           pabi1.append(integers2, r[0], r[1]);
           pabi1000.append(integers2, r[0], r[1]);
       }
       
       assert Arrays.equals(pabin.snapshot().toArray(new Number[]{}),
                            correctIntegers6);
       assert Arrays.equals(pabi0.snapshot().toArray(new Object[]{}), 
                            correctIntegers6);
       assert Arrays.equals(pabi1.snapshot().toArray(new Object[]{}),
                            correctIntegers6);
       assert Arrays.equals(pabi1000.snapshot().toArray(new Integer[]{}), 
                            correctIntegers6);
     
       // exception when length < 0
       try {
           pabin.append(integers2, 0, -23);
           assert false;
       } catch (IndexOutOfBoundsException iobe) {}
       
       /*
        * Powerlessness integrity: don't allow non-Powerless
        */
       PowerlessArray.Builder<Object> pabo = new PowerlessArray.Builder<Object>(42);
       
       try {
           pabo.append(new Object());
           assert false;
       } catch (ClassCastException cce) { }
       
       try {
           pabo.append(new org.joe_e.Token());
           assert false;
       } catch (ClassCastException cce) { }
       
       pabo.append(new String());
       
       try {
           pabo.append(new Object[]{"hello", "good-bye"});
           assert false;
       } catch (ClassCastException cce) { }
       
       try {
           pabo.append(new org.joe_e.Token[]{});
           assert false;
       } catch (ClassCastException cce) { }
       
       pabo.append(new String[]{"hi", "bye"});      
    }
}
