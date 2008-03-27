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
       
       int[] ints1 = {1, 2, -12, -34, 100, 10000, -1000, -10000};
       byte[] bytes = {1, 2, 3, 4, 5, -12, 34, 127};
       int[][] ranges = {{0, 8}, {1, 0}, {3, 5}, {7, 1}};

       for (int i : ints1) {
           babosn.write(i);
           babos0.write(i);
           babos1.write(i);
           babos1000.write(i);
       }
       
       byte[] bytes1 = {1, 2, -12, -34, 100, (byte) 10000, 
                          (byte) -1000, (byte) -10000}; 
       assert Arrays.equals(babosn.snapshot().toByteArray(), bytes1);
       assert Arrays.equals(babos0.snapshot().toByteArray(), bytes1);
       assert Arrays.equals(babos1.snapshot().toByteArray(), bytes1);
       assert Arrays.equals(babos1000.snapshot().toByteArray(), bytes1);
       assert babosn.length() == bytes1.length;
       assert babos0.length() == bytes1.length;
       assert babos1.length() == bytes1.length;
       assert babos1000.length() == bytes1.length;
              
       babosn.write(bytes);
       babos0.write(bytes);
       babos1.write(bytes);
       babos1000.write(bytes);
       
       byte[] bytes2 = {1, 2, -12, -34, 100, (byte) 10000,
                          (byte) -1000, (byte) -10000,
                          1, 2, 3, 4, 5, -12, 34, 127}; 
       assert Arrays.equals(babosn.snapshot().toByteArray(), bytes2);
       assert Arrays.equals(babos0.snapshot().toByteArray(), bytes2);
       assert Arrays.equals(babos1.snapshot().toByteArray(), bytes2);
       assert Arrays.equals(babos1000.snapshot().toByteArray(), bytes2);
       assert babosn.length() == bytes2.length;
       assert babos0.length() == bytes2.length;
       assert babos1.length() == bytes2.length;
       assert babos1000.length() == bytes2.length;
       
       for (int[] r : ranges) {           
           babosn.write(bytes, r[0], r[1]);
           babos0.write(bytes, r[0], r[1]);
           babos1.write(bytes, r[0], r[1]);
           babos1000.write(bytes, r[0], r[1]);
       }

       byte[] bytes3 = {1, 2, -12, -34, 100, (byte) 10000,
                          (byte) -1000, (byte) -10000,
                          1, 2, 3, 4, 5, -12, 34, 127,
                          1, 2, 3, 4, 5, -12, 34, 127,
                          4, 5, -12, 34, 127, 127}; 

       assert Arrays.equals(babosn.snapshot().toByteArray(), bytes3);
       assert Arrays.equals(babos0.snapshot().toByteArray(), bytes3);
       assert Arrays.equals(babos1.snapshot().toByteArray(), bytes3);
       assert Arrays.equals(babos1000.snapshot().toByteArray(), bytes3);
       assert babosn.length() == bytes3.length;
       assert babos0.length() == bytes3.length;
       assert babos1.length() == bytes3.length;
       assert babos1000.length() == bytes3.length;
              
       // test append(byte[]) from scratch
       babosn = new ByteArray.BuilderOutputStream(-23);
       babos0 = new ByteArray.BuilderOutputStream(0);
       babos1 = new ByteArray.BuilderOutputStream(1);
       babos1000 = new ByteArray.BuilderOutputStream(1000);
       
       babosn.write(bytes);
       babos0.write(bytes);
       babos1.write(bytes);
       babos1000.write(bytes);
       
       assert Arrays.equals(babosn.snapshot().toByteArray(), bytes);
       assert Arrays.equals(babos0.snapshot().toByteArray(), bytes);
       assert Arrays.equals(babos1.snapshot().toByteArray(), bytes);
       assert Arrays.equals(babos1000.snapshot().toByteArray(), bytes);
       assert babosn.length() == bytes.length;
       assert babos0.length() == bytes.length;
       assert babos1.length() == bytes.length;
       assert babos1000.length() == bytes.length;
       
       for (int i : ints1) {
           babosn.write(i);
           babos0.write(i);
           babos1.write(i);
           babos1000.write(i);
       }
       
       byte[] bytes4 = {1, 2, 3, 4, 5, -12, 34, 127,
                          1, 2, -12, -34, 100, (byte) 10000,
                          (byte) -1000, (byte) -10000};      
       
       assert Arrays.equals(babosn.snapshot().toByteArray(), bytes4);
       assert Arrays.equals(babos0.snapshot().toByteArray(), bytes4);
       assert Arrays.equals(babos1.snapshot().toByteArray(), bytes4);
       assert Arrays.equals(babos1000.snapshot().toByteArray(), bytes4);
       assert babosn.length() == bytes4.length;
       assert babos0.length() == bytes4.length;
       assert babos1.length() == bytes4.length;
       assert babos1000.length() == bytes4.length;
                     
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
       
       byte[] bytes5 = {1, 2, 3, 4, 5, -12, 34, 127,
                          4, 5, -12, 34, 127, 127}; 
       
       assert Arrays.equals(babosn.snapshot().toByteArray(), bytes5);
       assert Arrays.equals(babos0.snapshot().toByteArray(), bytes5);
       assert Arrays.equals(babos1.snapshot().toByteArray(), bytes5);
       assert Arrays.equals(babos1000.snapshot().toByteArray(), bytes5);
       assert babosn.length() == bytes5.length;
       assert babos0.length() == bytes5.length;
       assert babos1.length() == bytes5.length;
       assert babos1000.length() == bytes5.length;
       
       // exception when length < 0
       try {
           babosn.write(bytes, 0, -23);
           assert false;
       } catch (IndexOutOfBoundsException iobe) {}
       
       // ByteArray.asInputStream()
       ByteArray.Builder babNew = ByteArray.builder();
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
       
       assert babNew.snapshot().equals(someBytes);
       assert babNew.length() == someBytes.length();
       
       /*
        * ByteArray.Builder
        */
       ByteArray.Builder babn = ByteArray.builder(-23);
       ByteArray.Builder bab0 = ByteArray.builder(0);
       ByteArray.Builder bab1 = ByteArray.builder(1);
       ByteArray.Builder bab1000 = ByteArray.builder(1000);
       
       Byte[] wrappedBytes = {1, 2, 3, 4, 5, -12, 34, 127};
       
       for (byte b : bytes1) {
           babn.append((Byte) b);
           bab0.append(b);
           bab1.append((Byte) b);
           bab1000.append(b);
       }
       
       assert Arrays.equals(babn.snapshot().toByteArray(), bytes1);
       assert Arrays.equals(bab0.snapshot().toByteArray(), bytes1);
       assert Arrays.equals(bab1.snapshot().toByteArray(), bytes1);
       assert Arrays.equals(bab1000.snapshot().toByteArray(), bytes1);
       assert babn.length() == bytes1.length;
       assert bab0.length() == bytes1.length;
       assert bab1.length() == bytes1.length;
       assert bab1000.length() == bytes1.length;
       
       babn.append(bytes);
       bab0.append(wrappedBytes);
       bab1.append(bytes);
       bab1000.append(wrappedBytes);
       
       assert Arrays.equals(babn.snapshot().toByteArray(), bytes2);
       assert Arrays.equals(bab0.snapshot().toByteArray(), bytes2);
       assert Arrays.equals(bab1.snapshot().toByteArray(), bytes2);
       assert Arrays.equals(bab1000.snapshot().toByteArray(), bytes2);
       assert babn.length() == bytes2.length;
       assert bab0.length() == bytes2.length;
       assert bab1.length() == bytes2.length;
       assert bab1000.length() == bytes2.length;
       
       for (int[] r : ranges) {           
           babn.append(wrappedBytes, r[0], r[1]);
           bab0.append(bytes, r[0], r[1]);
           bab1.append(wrappedBytes, r[0], r[1]);
           bab1000.append(bytes, r[0], r[1]);
       }

       assert Arrays.equals(babn.snapshot().toByteArray(), bytes3);
       assert Arrays.equals(bab0.snapshot().toByteArray(), bytes3);
       assert Arrays.equals(bab1.snapshot().toByteArray(), bytes3);
       assert Arrays.equals(bab1000.snapshot().toByteArray(), bytes3);
       assert babn.length() == bytes3.length;
       assert bab0.length() == bytes3.length;
       assert bab1.length() == bytes3.length;
       assert bab1000.length() == bytes3.length;
       
       // test append(byte[]) from scratch
       babn = ByteArray.builder(-23);
       bab0 = ByteArray.builder(0);
       bab1 = ByteArray.builder(1);
       bab1000 = ByteArray.builder(1000);
       
       babn.append(bytes);
       bab0.append(wrappedBytes);
       bab1.append(bytes);
       bab1000.append(wrappedBytes);

       assert Arrays.equals(babn.snapshot().toByteArray(), bytes);
       assert Arrays.equals(bab0.snapshot().toByteArray(), bytes);
       assert Arrays.equals(bab1.snapshot().toByteArray(), bytes);
       assert Arrays.equals(bab1000.snapshot().toByteArray(), bytes);
       assert babn.length() == bytes.length;
       assert bab0.length() == bytes.length;
       assert bab1.length() == bytes.length;
       assert bab1000.length() == bytes.length;

       for (byte b : bytes1) {
           babn.append(b);
           bab0.append((Byte) b);
           bab1.append((Byte) b);
           bab1000.append(b);
       }
       
       assert Arrays.equals(babn.snapshot().toByteArray(), bytes4);
       assert Arrays.equals(bab0.snapshot().toByteArray(), bytes4);
       assert Arrays.equals(bab1.snapshot().toByteArray(), bytes4);
       assert Arrays.equals(bab1000.snapshot().toByteArray(), bytes4);
       assert babn.length() == bytes4.length;
       assert bab0.length() == bytes4.length;
       assert bab1.length() == bytes4.length;
       assert bab1000.length() == bytes4.length;
       
       // test append(byte[], int, int) from scratch
       babn = ByteArray.builder(-23);
       bab0 = ByteArray.builder(0);
       bab1 = ByteArray.builder(1);
       bab1000 = ByteArray.builder(1000);
       
       for (int[] r : ranges) {           
           babn.append(wrappedBytes, r[0], r[1]);
           bab0.append(bytes, r[0], r[1]);
           bab1.append(bytes, r[0], r[1]);
           bab1000.append(wrappedBytes, r[0], r[1]);
       }
       
       assert Arrays.equals(babn.snapshot().toByteArray(), bytes5);
       assert Arrays.equals(bab0.snapshot().toByteArray(), bytes5);
       assert Arrays.equals(bab1.snapshot().toByteArray(), bytes5);
       assert Arrays.equals(bab1000.snapshot().toByteArray(), bytes5);
       assert babn.length() == bytes5.length;
       assert bab0.length() == bytes5.length;
       assert bab1.length() == bytes5.length;
       assert bab1000.length() == bytes5.length;
     
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
       CharArray.Builder cabn = CharArray.builder(-23);
       CharArray.Builder cab0 = CharArray.builder(0);
       CharArray.Builder cab1 = CharArray.builder(1);
       CharArray.Builder cab1000 = CharArray.builder(1000);
       
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
       assert cabn.length() == chars1.length;
       assert cab0.length() == chars1.length;
       assert cab1.length() == chars1.length;
       assert cab1000.length() == chars1.length;
       
       cabn.append(characters1);
       cab0.append(chars1);
       cab1.append(characters1);
       cab1000.append(chars1);

       char[] chars2 = {'a', 'b', 'A', 'B', '*', '`', 'õ', 'ß',
                               'a', 'b', 'A', 'B', '*', '`', 'õ', 'ß'};

       assert Arrays.equals(cabn.snapshot().toCharArray(), chars2);
       assert Arrays.equals(cab0.snapshot().toCharArray(), chars2);
       assert Arrays.equals(cab1.snapshot().toCharArray(), chars2);
       assert Arrays.equals(cab1000.snapshot().toCharArray(), chars2);
       assert cabn.length() == chars2.length;
       assert cab0.length() == chars2.length;
       assert cab1.length() == chars2.length;
       assert cab1000.length() == chars2.length;
       
       for (int[] r : ranges) {           
           cabn.append(chars1, r[0], r[1]);
           cab0.append(characters1, r[0], r[1]);
           cab1.append(chars1, r[0], r[1]);
           cab1000.append(characters1, r[0], r[1]);
       }

       char[] chars3 = {'a', 'b', 'A', 'B', '*', '`', 'õ', 'ß',
                               'a', 'b', 'A', 'B', '*', '`', 'õ', 'ß',
                               'a', 'b', 'A', 'B', '*', '`', 'õ', 'ß',
                               'B', '*', '`', 'õ', 'ß', 'ß'};

       assert Arrays.equals(cabn.snapshot().toCharArray(), chars3);
       assert Arrays.equals(cab0.snapshot().toCharArray(), chars3);
       assert Arrays.equals(cab1.snapshot().toCharArray(), chars3);
       assert Arrays.equals(cab1000.snapshot().toCharArray(), chars3);
       assert cabn.length() == chars3.length;
       assert cab0.length() == chars3.length;
       assert cab1.length() == chars3.length;
       assert cab1000.length() == chars3.length;
       
       // test append(char[]) from scratch
       cabn = CharArray.builder(-23);
       cab0 = CharArray.builder(0);
       cab1 = CharArray.builder(1);
       cab1000 = CharArray.builder(1000);
       
       cabn.append(chars1);
       cab0.append(characters1);
       cab1.append(chars1);
       cab1000.append(characters1);

       assert Arrays.equals(cabn.snapshot().toCharArray(), chars1);
       assert Arrays.equals(cab0.snapshot().toCharArray(), chars1);
       assert Arrays.equals(cab1.snapshot().toCharArray(), chars1);
       assert Arrays.equals(cab1000.snapshot().toCharArray(), chars1);
       assert cabn.length() == chars1.length;
       assert cab0.length() == chars1.length;
       assert cab1.length() == chars1.length;
       assert cab1000.length() == chars1.length;

       for (char ch : chars1) {
           cabn.append(ch);
           cab0.append(ch);
           cab1.append(ch);
           cab1000.append(ch);
       }

       assert Arrays.equals(cabn.snapshot().toCharArray(), chars2);
       assert Arrays.equals(cab0.snapshot().toCharArray(), chars2);
       assert Arrays.equals(cab1.snapshot().toCharArray(), chars2);
       assert Arrays.equals(cab1000.snapshot().toCharArray(), chars2);
       assert cabn.length() == chars2.length;
       assert cab0.length() == chars2.length;
       assert cab1.length() == chars2.length;
       assert cab1000.length() == chars2.length;
                     
       // test append(char[], int, int) from scratch
       cabn = CharArray.builder(-23);
       cab0 = CharArray.builder(0);
       cab1 = CharArray.builder(1);
       cab1000 = CharArray.builder(1000);
       
       for (int[] r : ranges) {           
           cabn.append(characters1, r[0], r[1]);
           cab0.append(chars1, r[0], r[1]);
           cab1.append(characters1, r[0], r[1]);
           cab1000.append(chars1, r[0], r[1]);
       }
       
       char[] chars4 = {'a', 'b', 'A', 'B', '*', '`', 'õ', 'ß',
                              'B', '*', '`', 'õ', 'ß', 'ß'}; 
       
       assert Arrays.equals(cabn.snapshot().toCharArray(), chars4);
       assert Arrays.equals(cab0.snapshot().toCharArray(), chars4);
       assert Arrays.equals(cab1.snapshot().toCharArray(), chars4);
       assert Arrays.equals(cab1000.snapshot().toCharArray(), chars4);
       assert cabn.length() == chars4.length;
       assert cab0.length() == chars4.length;
       assert cab1.length() == chars4.length;
       assert cab1000.length() == chars4.length;
     
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
       ConstArray.Builder<Integer> cabin = ConstArray.builder(-23);
       ConstArray.Builder<Integer> cabi0 = ConstArray.builder(0);
       ConstArray.Builder<Integer> cabi1 = ConstArray.builder(1);
       ConstArray.Builder<Integer> cabi1000 = ConstArray.builder(1000);
       
       Integer[] integers1 = {1, 2, -12, -34, 100, 10000, -1000, -10000};
       
       for (int i : ints1) {
           cabin.append(i);
           cabi0.append(i);
           cabi1.append(i);
           cabi1000.append(i);
       }
             
       assert Arrays.equals(cabin.snapshot().toArray(new Object[]{}), 
                            integers1);
       assert Arrays.equals(cabi0.snapshot().toArray(new Integer[]{}), 
                            integers1);
       assert Arrays.equals(cabi1.snapshot().toArray(new Object[]{}), 
                            integers1);
       assert Arrays.equals(cabi1000.snapshot().toArray(new Number[]{}), 
                            integers1);
       assert cabin.length() == integers1.length;
       assert cabi0.length() == integers1.length;
       assert cabi1.length() == integers1.length;
       assert cabi1000.length() == integers1.length;
       
       cabin.append(integers1);
       cabi0.append(integers1);
       cabi1.append(integers1);
       cabi1000.append(integers1);

       Integer[] integers2 = {1, 2, -12, -34, 100, 10000, -1000, -10000,
                              1, 2, -12, -34, 100, 10000, -1000, -10000};

       assert Arrays.equals(cabin.snapshot().toArray(new Number[]{}), 
                            integers2);
       assert Arrays.equals(cabi0.snapshot().toArray(new Object[]{}), 
                            integers2);
       assert Arrays.equals(cabi1.snapshot().toArray(new Integer[]{}), 
                            integers2);
       assert Arrays.equals(cabi1000.snapshot().toArray(new Object[]{}), 
                            integers2);
       assert cabin.length() == integers2.length;
       assert cabi0.length() == integers2.length;
       assert cabi1.length() == integers2.length;
       assert cabi1000.length() == integers2.length;       
       
       for (int[] r : ranges) {           
           cabin.append(integers1, r[0], r[1]);
           cabi0.append(integers1, r[0], r[1]);
           cabi1.append(integers1, r[0], r[1]);
           cabi1000.append(integers1, r[0], r[1]);
       }

       Integer[] integers3 = {1, 2, -12, -34, 100, 10000, -1000, -10000,
                              1, 2, -12, -34, 100, 10000, -1000, -10000,
                              1, 2, -12, -34, 100, 10000, -1000, -10000,
                              -34, 100, 10000, -1000, -10000, -10000};

       assert Arrays.equals(cabin.snapshot().toArray(new Object[]{}), 
                            integers3);
       assert Arrays.equals(cabi0.snapshot().toArray(new Number[]{}), 
                            integers3);
       assert Arrays.equals(cabi1.snapshot().toArray(new Object[]{}), 
                            integers3);
       assert Arrays.equals(cabi1000.snapshot().toArray(new Integer[]{}), 
                            integers3);
       assert cabin.length() == integers3.length;
       assert cabi0.length() == integers3.length;
       assert cabi1.length() == integers3.length;
       assert cabi1000.length() == integers3.length;       
       
       // test append(Integer[]) from scratch
       cabin = ConstArray.builder(-23);
       cabi0 = ConstArray.builder(0);
       cabi1 = ConstArray.builder(1);
       cabi1000 = ConstArray.builder(1000);
       
       cabin.append(integers1);
       cabi0.append(integers1);
       cabi1.append(integers1);
       cabi1000.append(integers1);

       assert Arrays.equals(cabin.snapshot().toArray(new Integer[]{}),
                            integers1);
       assert Arrays.equals(cabi0.snapshot().toArray(new Object[]{}),
                            integers1);
       assert Arrays.equals(cabi1.snapshot().toArray(new Number[]{}),
                            integers1);
       assert Arrays.equals(cabi1000.snapshot().toArray(new Object[]{}),
                            integers1);
       assert cabin.length() == integers1.length;
       assert cabi0.length() == integers1.length;
       assert cabi1.length() == integers1.length;
       assert cabi1000.length() == integers1.length;       
       
       for (Integer i : ints1) {
           cabin.append(i);
           cabi0.append(i);
           cabi1.append(i);
           cabi1000.append(i);
       }

       assert Arrays.equals(cabin.snapshot().toArray(new Object[]{}), 
                            integers2);
       assert Arrays.equals(cabi0.snapshot().toArray(new Integer[]{}), 
                            integers2);
       assert Arrays.equals(cabi1.snapshot().toArray(new Number[]{}), 
                            integers2);
       assert Arrays.equals(cabi1000.snapshot().toArray(new Object[]{}), 
                            integers2);
       assert cabin.length() == integers2.length;
       assert cabi0.length() == integers2.length;
       assert cabi1.length() == integers2.length;
       assert cabi1000.length() == integers2.length;       
       
       // test append(char[], int, int) from scratch
       // test append(Integer[]) from scratch
       cabin = ConstArray.builder(-23);
       cabi0 = ConstArray.builder(0);
       cabi1 = ConstArray.builder(1);
       cabi1000 = ConstArray.builder(1000);
   
       for (int[] r : ranges) {           
           cabin.append(integers1, r[0], r[1]);
           cabi0.append(integers1, r[0], r[1]);
           cabi1.append(integers1, r[0], r[1]);
           cabi1000.append(integers1, r[0], r[1]);
       }
       
       Integer[] integers4 = {1, 2, -12, -34, 100, 10000, -1000, -10000,
                              -34, 100, 10000, -1000, -10000, -10000};
       
       assert Arrays.equals(cabin.snapshot().toArray(new Number[]{}), 
                            integers4);
       assert Arrays.equals(cabi0.snapshot().toArray(new Object[]{}), 
                            integers4);
       assert Arrays.equals(cabi1.snapshot().toArray(new Object[]{}),
                            integers4);
       assert Arrays.equals(cabi1000.snapshot().toArray(new Integer[]{}), 
                            integers4);
       assert cabin.length() == integers4.length;
       assert cabi0.length() == integers4.length;
       assert cabi1.length() == integers4.length;
       assert cabi1000.length() == integers4.length;    
       
       // exception when length < 0
       try {
           cabin.append(integers1, 0, -23);
           assert false;
       } catch (IndexOutOfBoundsException iobe) {}

       /*
        * ImmutableArray
        * 
        * boring stuff - basics
        */      
       ImmutableArray.Builder<Integer> iabin = ImmutableArray.builder(-23);
       ImmutableArray.Builder<Integer> iabi0 = ImmutableArray.builder(0);
       ImmutableArray.Builder<Integer> iabi1 = ImmutableArray.builder(1);
       ImmutableArray.Builder<Integer> iabi1000 = ImmutableArray.builder(1000);
             
       for (int i : ints1) {
           iabin.append(i);
           iabi0.append(i);
           iabi1.append(i);
           iabi1000.append(i);
       }
             
       assert Arrays.equals(iabin.snapshot().toArray(new Object[]{}), 
                            integers1);
       assert Arrays.equals(iabi0.snapshot().toArray(new Integer[]{}), 
                            integers1);
       assert Arrays.equals(iabi1.snapshot().toArray(new Object[]{}), 
                            integers1);
       assert Arrays.equals(iabi1000.snapshot().toArray(new Number[]{}), 
                            integers1);
       assert iabin.length() == integers1.length;
       assert iabi0.length() == integers1.length;
       assert iabi1.length() == integers1.length;
       assert iabi1000.length() == integers1.length;    
       
       iabin.append(integers1);
       iabi0.append(integers1);
       iabi1.append(integers1);
       iabi1000.append(integers1);

       assert Arrays.equals(iabin.snapshot().toArray(new Number[]{}), 
                            integers2);
       assert Arrays.equals(iabi0.snapshot().toArray(new Object[]{}), 
                            integers2);
       assert Arrays.equals(iabi1.snapshot().toArray(new Integer[]{}), 
                            integers2);
       assert Arrays.equals(iabi1000.snapshot().toArray(new Object[]{}), 
                            integers2);
       assert iabin.length() == integers2.length;
       assert iabi0.length() == integers2.length;
       assert iabi1.length() == integers2.length;
       assert iabi1000.length() == integers2.length;    
       
       for (int[] r : ranges) {           
           iabin.append(integers1, r[0], r[1]);
           iabi0.append(integers1, r[0], r[1]);
           iabi1.append(integers1, r[0], r[1]);
           iabi1000.append(integers1, r[0], r[1]);
       }

       assert Arrays.equals(iabin.snapshot().toArray(new Object[]{}), 
                            integers3);
       assert Arrays.equals(iabi0.snapshot().toArray(new Number[]{}), 
                            integers3);
       assert Arrays.equals(iabi1.snapshot().toArray(new Object[]{}), 
                            integers3);
       assert Arrays.equals(iabi1000.snapshot().toArray(new Integer[]{}), 
                            integers3);
       assert iabin.length() == integers3.length;
       assert iabi0.length() == integers3.length;
       assert iabi1.length() == integers3.length;
       assert iabi1000.length() == integers3.length;    
       
       // test append(Integer[]) from scratch
       iabin = ImmutableArray.builder(-23);
       iabi0 = ImmutableArray.builder(0);
       iabi1 = ImmutableArray.builder(1);
       iabi1000 = ImmutableArray.builder(1000);
       
       iabin.append(integers1);
       iabi0.append(integers1);
       iabi1.append(integers1);
       iabi1000.append(integers1);

       assert Arrays.equals(iabin.snapshot().toArray(new Integer[]{}),
                            integers1);
       assert Arrays.equals(iabi0.snapshot().toArray(new Object[]{}),
                            integers1);
       assert Arrays.equals(iabi1.snapshot().toArray(new Number[]{}),
                            integers1);
       assert Arrays.equals(iabi1000.snapshot().toArray(new Object[]{}),
                            integers1);
       assert iabin.length() == integers1.length;
       assert iabi0.length() == integers1.length;
       assert iabi1.length() == integers1.length;
       assert iabi1000.length() == integers1.length;    

       for (Integer i : ints1) {
           iabin.append(i);
           iabi0.append(i);
           iabi1.append(i);
           iabi1000.append(i);
       }

       assert Arrays.equals(iabin.snapshot().toArray(new Object[]{}), 
                            integers2);
       assert Arrays.equals(iabi0.snapshot().toArray(new Integer[]{}), 
                            integers2);
       assert Arrays.equals(iabi1.snapshot().toArray(new Number[]{}), 
                            integers2);
       assert Arrays.equals(iabi1000.snapshot().toArray(new Object[]{}), 
                            integers2);
       assert iabin.length() == integers2.length;
       assert iabi0.length() == integers2.length;
       assert iabi1.length() == integers2.length;
       assert iabi1000.length() == integers2.length;
                     
       // test append(char[], int, int) from scratch
       // test append(Integer[]) from scratch
       iabin = ImmutableArray.builder(-23);
       iabi0 = ImmutableArray.builder(0);
       iabi1 = ImmutableArray.builder(1);
       iabi1000 = ImmutableArray.builder(1000);
   
       for (int[] r : ranges) {           
           iabin.append(integers1, r[0], r[1]);
           iabi0.append(integers1, r[0], r[1]);
           iabi1.append(integers1, r[0], r[1]);
           iabi1000.append(integers1, r[0], r[1]);
       }
       
       assert Arrays.equals(iabin.snapshot().toArray(new Number[]{}),
                            integers4);
       assert Arrays.equals(iabi0.snapshot().toArray(new Object[]{}), 
                            integers4);
       assert Arrays.equals(iabi1.snapshot().toArray(new Object[]{}),
                            integers4);
       assert Arrays.equals(iabi1000.snapshot().toArray(new Integer[]{}), 
                            integers4);
       assert iabin.length() == integers4.length;
       assert iabi0.length() == integers4.length;
       assert iabi1.length() == integers4.length;
       assert iabi1000.length() == integers4.length;    
     
       // exception when length < 0
       try {
           iabin.append(integers1, 0, -23);
           assert false;
       } catch (IndexOutOfBoundsException iobe) {}

       
       /*
        * Immutableness integrity: don't allow non-immutables
        */
       ImmutableArray.Builder<Object> iabo = ImmutableArray.builder(42);
       
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
       PowerlessArray.Builder<Integer> pabin = PowerlessArray.builder(-23);
       PowerlessArray.Builder<Integer> pabi0 = PowerlessArray.builder(0);
       PowerlessArray.Builder<Integer> pabi1 = PowerlessArray.builder(1);
       PowerlessArray.Builder<Integer> pabi1000 = PowerlessArray.builder(1000);
             
       for (int i : ints1) {
           pabin.append(i);
           pabi0.append(i);
           pabi1.append(i);
           pabi1000.append(i);
       }
             
       assert Arrays.equals(pabin.snapshot().toArray(new Object[]{}), 
                            integers1);
       assert Arrays.equals(pabi0.snapshot().toArray(new Integer[]{}), 
                            integers1);
       assert Arrays.equals(pabi1.snapshot().toArray(new Object[]{}), 
                            integers1);
       assert Arrays.equals(pabi1000.snapshot().toArray(new Number[]{}), 
                            integers1);
       assert pabin.length() == integers1.length;
       assert pabi0.length() == integers1.length;
       assert pabi1.length() == integers1.length;
       assert pabi1000.length() == integers1.length;    
              
       pabin.append(integers1);
       pabi0.append(integers1);
       pabi1.append(integers1);
       pabi1000.append(integers1);

       assert Arrays.equals(pabin.snapshot().toArray(new Number[]{}), 
                            integers2);
       assert Arrays.equals(pabi0.snapshot().toArray(new Object[]{}), 
                            integers2);
       assert Arrays.equals(pabi1.snapshot().toArray(new Integer[]{}), 
                            integers2);
       assert Arrays.equals(pabi1000.snapshot().toArray(new Object[]{}), 
                            integers2);
       assert pabin.length() == integers2.length;
       assert pabi0.length() == integers2.length;
       assert pabi1.length() == integers2.length;
       assert pabi1000.length() == integers2.length;
       
       for (int[] r : ranges) {           
           pabin.append(integers1, r[0], r[1]);
           pabi0.append(integers1, r[0], r[1]);
           pabi1.append(integers1, r[0], r[1]);
           pabi1000.append(integers1, r[0], r[1]);
       }

       assert Arrays.equals(pabin.snapshot().toArray(new Object[]{}), 
                            integers3);
       assert Arrays.equals(pabi0.snapshot().toArray(new Number[]{}), 
                            integers3);
       assert Arrays.equals(pabi1.snapshot().toArray(new Object[]{}), 
                            integers3);
       assert Arrays.equals(pabi1000.snapshot().toArray(new Integer[]{}), 
                            integers3);
       assert pabin.length() == integers3.length;
       assert pabi0.length() == integers3.length;
       assert pabi1.length() == integers3.length;
       assert pabi1000.length() == integers3.length;
       
       // test append(Integer[]) from scratch
       pabin = PowerlessArray.builder(-23);
       pabi0 = PowerlessArray.builder(0);
       pabi1 = PowerlessArray.builder(1);
       pabi1000 = PowerlessArray.builder(1000);
       
       pabin.append(integers1);
       pabi0.append(integers1);
       pabi1.append(integers1);
       pabi1000.append(integers1);

       assert Arrays.equals(pabin.snapshot().toArray(new Integer[]{}),
                            integers1);
       assert Arrays.equals(pabi0.snapshot().toArray(new Object[]{}),
                            integers1);
       assert Arrays.equals(pabi1.snapshot().toArray(new Number[]{}),
                            integers1);
       assert Arrays.equals(pabi1000.snapshot().toArray(new Object[]{}),
                            integers1);
       assert pabin.length() == integers1.length;
       assert pabi0.length() == integers1.length;
       assert pabi1.length() == integers1.length;
       assert pabi1000.length() == integers1.length;    

       for (Integer i : ints1) {
           pabin.append(i);
           pabi0.append(i);
           pabi1.append(i);
           pabi1000.append(i);
       }

       assert Arrays.equals(pabin.snapshot().toArray(new Object[]{}), 
                            integers2);
       assert Arrays.equals(pabi0.snapshot().toArray(new Integer[]{}), 
                            integers2);
       assert Arrays.equals(pabi1.snapshot().toArray(new Number[]{}), 
                            integers2);
       assert Arrays.equals(pabi1000.snapshot().toArray(new Object[]{}), 
                            integers2);       
       assert pabin.length() == integers2.length;
       assert pabi0.length() == integers2.length;
       assert pabi1.length() == integers2.length;
       assert pabi1000.length() == integers2.length;
                     
       // test append(char[], int, int) from scratch
       // test append(Integer[]) from scratch
       pabin = PowerlessArray.builder(-23);
       pabi0 = PowerlessArray.builder(0);
       pabi1 = PowerlessArray.builder(1);
       pabi1000 = PowerlessArray.builder(1000);
   
       for (int[] r : ranges) {           
           pabin.append(integers1, r[0], r[1]);
           pabi0.append(integers1, r[0], r[1]);
           pabi1.append(integers1, r[0], r[1]);
           pabi1000.append(integers1, r[0], r[1]);
       }
       
       assert Arrays.equals(pabin.snapshot().toArray(new Number[]{}),
                            integers4);
       assert Arrays.equals(pabi0.snapshot().toArray(new Object[]{}), 
                            integers4);
       assert Arrays.equals(pabi1.snapshot().toArray(new Object[]{}),
                            integers4);
       assert Arrays.equals(pabi1000.snapshot().toArray(new Integer[]{}), 
                            integers4);
       assert pabin.length() == integers4.length;
       assert pabi0.length() == integers4.length;
       assert pabi1.length() == integers4.length;
       assert pabi1000.length() == integers4.length;
     
       // exception when length < 0
       try {
           pabin.append(integers1, 0, -23);
           assert false;
       } catch (IndexOutOfBoundsException iobe) {}
       
       /*
        * Powerlessness integrity: don't allow non-Powerless
        */
       PowerlessArray.Builder<Object> pabo = PowerlessArray.builder(42);
       
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
