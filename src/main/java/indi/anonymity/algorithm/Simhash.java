package indi.anonymity.algorithm;

import java.math.BigInteger;

/**
 * Created by emily on 17/2/15.
 */
public class Simhash {

    private String[] tokens;
    private String strSimhash;
    private BigInteger intSimhash;
    private int hashbits = 64;

    public Simhash(String[] tokens) {
        this.tokens = tokens;
        this.intSimhash = this.generateSimhash();
    }

    public Simhash(String[] tokens, int hashbits) {
        this.tokens = tokens;
        this.hashbits = hashbits;
        this.intSimhash = this.generateSimhash();
    }

    public String getStrSimhash() {
        return strSimhash;
    }

    public void setStrSimhash(String strSimhash) {
        this.strSimhash = strSimhash;
    }

    public BigInteger getIntSimhash() {
        return intSimhash;
    }

    public void setIntSimhash(BigInteger intSimhash) {
        this.intSimhash = intSimhash;
    }

    private BigInteger generateSimhash() {
        int[] v = new int[this.hashbits];
        for(int i = 0, len = this.tokens.length; i < len; i++) {
            BigInteger tmp = this.hash(tokens[i]);
            for(int j = 0; j < this.hashbits; j++) {
                BigInteger bitMask = new BigInteger("1").shiftLeft(j);
                if(tmp.and(bitMask).signum() != 0) {
                    v[j]++;
                } else {
                    v[j]--;
                }
            }
        }
        BigInteger fingerPrint = new BigInteger("0");
        StringBuffer simhashBuffer = new StringBuffer();
        for(int i = 0; i < this.hashbits; i++) {
            if(v[i] >= 0) {
                fingerPrint = fingerPrint.add(new BigInteger("1").shiftLeft(i));
                simhashBuffer.append(1);
            } else {
                simhashBuffer.append(0);
            }
        }
        this.strSimhash = simhashBuffer.toString();
        return fingerPrint;
    }

    private BigInteger hash(String source) {
        if(source == null || source.length() == 0) {
            return new BigInteger("0");
        } else {
            char[] sourceArray = source.toCharArray();
            BigInteger x = BigInteger.valueOf(((long)sourceArray[0]) << 7);
            BigInteger m = new BigInteger("1000003");
            BigInteger mask = new BigInteger("2").pow(this.hashbits).subtract(new BigInteger("1"));
            for(char item: sourceArray) {
                BigInteger tmp = BigInteger.valueOf((long)item);
                x = x.multiply(m).xor(tmp).and(mask);
            }
            x = x.xor(new BigInteger(String.valueOf(source.length())));
            if(x.equals(new BigInteger("-1"))) {
                x = new BigInteger("-2");
            }
            return x;
        }
    }


}
