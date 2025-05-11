package org.example;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class Schnorr {

    // parametry
    private BigInteger p, q, a, s, v;
    private final SecureRandom random = new SecureRandom();
    private final int pBits = 512; // dlugosc p
    private final int qBits = 140; // dlugosc q
    private MessageDigest digest;

    public Schnorr() {
        generate();

        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not supported", e);
        }
    }

    public BigInteger getP() { return p; }
    public BigInteger getQ() { return q; }
    public BigInteger getA() { return a; }
    public BigInteger getPrivateKey() { return s; }
    public BigInteger getPublicKey() { return v; }

    public void generate(){
        // krok 1: genrujemy q jako liczbe pierwsza
        q = BigInteger.probablePrime(qBits, random);

        // krok 2: znajdujemy takie p, zeby q bylo dzielnikiem p-1. czyli p = q * r + 1, takie zeby p byla liczba pierwsza
        BigInteger r, candidateP;
        do {
            r = new BigInteger(pBits - q.bitLength(), random);
            candidateP = q.multiply(r).add(BigInteger.ONE);
        } while (!candidateP.isProbablePrime(64));
        p = candidateP;

        // krok 3: znajdujemy a (rozne od 1), takie ze a^q ≡ 1 mod p
        BigInteger g;
        do {
            g = new BigInteger(pBits - 1, random);
            a = g.modPow(p.subtract(BigInteger.ONE).divide(q), p);
        } while (a.compareTo(BigInteger.ONE) <= 0);

        // krok 4: generujemy klucz prywatny s ∈ [1, q-1]
        do {
            s = new BigInteger(qBits - 1, random);
        } while (s.compareTo(BigInteger.ZERO) <= 0 || s.compareTo(q) >= 0);

        // krok 5: obliczamy klucz publiczny v = a^(-s) mod p
        v = a.modPow(s, p).modInverse(p);
    }

    public BigInteger[] sign(byte[] message) {
        // krok 1: wybieramy losowa liczbe r < q
        BigInteger r;
        do {
            r = new BigInteger(q.bitLength(), random);
        } while (r.compareTo(q) >= 0 || r.compareTo(BigInteger.ZERO) <= 0);

        // krok 2: x = a^r mod p
        BigInteger x = a.modPow(r, p);

        // krok 3: e = H(M || x)
        byte[] xBytes = x.toByteArray();
        byte[] concatenated = new byte[message.length + xBytes.length];
        System.arraycopy(message, 0, concatenated, 0, message.length);
        System.arraycopy(xBytes, 0, concatenated, message.length, xBytes.length);
        digest.digest(concatenated);
        BigInteger e = new BigInteger(1, digest.digest()).mod(q); // ograniczamy e do [0,q)

        // krok 4: y = (r + s * e) mod q
        BigInteger y = r.add(s.multiply(e)).mod(q);

        // RETURN signature (e,y)
        return new BigInteger[]{e, y};
    }

    public BigInteger[] sign(String message) {
        return sign(message.getBytes());
    }

    public boolean verify(byte[] message, BigInteger[] signature) {
        BigInteger e = signature[0];
        BigInteger y = signature[1];

        // krok 1: obliczamy x' = a^y * v^e mod p
        BigInteger x1 = a.modPow(y, p); // a^y mod p
        BigInteger x2 = a.modPow(e, p); // v^e mod p
        BigInteger xPrime = x1.multiply(x2).mod(p); // a^y * v^e mod p

        // krok 2: obliczamy e' = H (M || x')
        byte[] xBytes = xPrime.toByteArray();
        byte[] combined = new byte[message.length + xBytes.length];
        System.arraycopy(message, 0, combined, 0, message.length);
        System.arraycopy(xBytes, 0, combined, message.length, xBytes.length);
        digest.update(combined);
        BigInteger ePrime = new BigInteger(1, digest.digest()).mod(q);

        // krok 3: porownujemy e' z odebranym e
        return ePrime.equals(e);
    }

    public boolean verify(String message, BigInteger[] signature) {
        return verify(message.getBytes(), signature);
    }
}
