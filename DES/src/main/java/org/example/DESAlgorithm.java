package org.example;

import java.math.BigInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DESAlgorithm {
    private static final Logger logger = LoggerFactory.getLogger(DESAlgorithm.class);

    //    BigInteger originalMessage = new BigInteger("0123456789ABCDEF", 16);
//    BigInteger originalKey = new BigInteger("0123456789ABCDEF", 16);

    // tablica permutacji
    final byte[] IP = {
            58, 50, 42, 34, 26, 18, 10, 2,
            60, 52, 44, 36, 28, 20, 12, 4,
            62, 54, 46, 38, 30, 22, 14, 6,
            64, 56, 48, 40, 32, 24, 16, 8,
            57, 49, 41, 33, 25, 17, 9, 1,
            59, 51, 43, 35, 27, 19, 11, 3,
            61, 53, 45, 37, 29, 21, 13, 5,
            63, 55, 47, 39, 31, 23, 15, 7
    };

    // "Permuted Choice 1" / "Key Permutation"
    // tablica permutacji dla klucza (64-bit -> 56-bit, pomijamy 8 bit kazdego bajta)
    final byte[] PC1 = {
            57, 49, 41, 33, 25, 17, 9,
            1, 58, 50, 42, 34, 26, 18,
            10, 2, 59, 51, 43, 35, 27,
            19, 11, 3, 60, 52, 44, 36,
            63, 55, 47, 39, 31, 23, 15,
            7, 62, 54, 46, 38, 30, 22,
            14, 6, 61, 53, 45, 37, 29,
            21, 13, 5, 28, 20, 12, 4
    };

    // "Permuted Choice 2" / "Compression Permutation"
    // (56-bit -> 48-bit) 8 bits are dropped, 48 bits are permuted
    final byte[] PC2 = {
            14, 17, 11, 24, 1, 5, 3, 28, 15, 6, 21, 10,
            23, 19, 12, 4, 26, 8, 16, 7, 27, 20, 13, 2,
            41, 52, 31, 37, 47, 55, 30, 40, 51, 45, 33, 48,
            44, 49, 39, 56, 34, 53, 46, 42, 50, 36, 29, 32
    };

    // number of key bits shifted per round
    final byte[] SHIFTS = {1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1};

    // "Expansion"
    final byte[] E = {
            32, 1, 2, 3, 4, 5, 4, 5, 6, 7, 8, 9,
            8, 9, 10, 11, 12, 13, 12, 13, 14, 15, 16, 17,
            16, 17, 18, 19, 20, 21, 20, 21, 22, 23, 24, 25,
            24, 25, 26, 27, 28, 29, 28, 29, 30, 31, 32, 1
    };

    // "Substitution boxes"
    final byte[][][] SBOX = {
            { // S1
                    {14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7},
                    {0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8},
                    {4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0},
                    {15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13}
            },
            { // S2
                    {15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 2, 13, 12, 0, 5, 10},
                    {3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5},
                    {0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15},
                    {13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9}
            },
            { // S3
                    {10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8},
                    {13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1},
                    {13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7},
                    {1, 10, 13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 11, 5, 2, 12}
            },
            { // S4
                    {7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15},
                    {13, 8, 11, 5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9},
                    {10, 6, 9, 0, 12, 11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4},
                    {3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 12, 7, 2, 14}
            },
            { // S5
                    {2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9},
                    {14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6},
                    {4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14},
                    {11, 8, 12, 7, 1, 14, 2, 13, 6, 15, 0, 9, 10, 4, 5, 3}
            },
            { // S6
                    {12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11},
                    {10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8},
                    {9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6},
                    {4, 3, 2, 12, 9, 5, 15, 10, 11, 14, 1, 7, 6, 0, 8, 13}
            },
            { // S7
                    {4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1},
                    {13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6},
                    {1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2},
                    {6, 11, 13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 14, 2, 3, 12}
            },
            { // S8
                    {13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7},
                    {1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2},
                    {7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8},
                    {2, 1, 14, 7, 4, 10, 8, 13, 15, 12, 9, 0, 3, 5, 6, 11}
            }
    };

    // "P-Box"
    final byte[] P = {
            16, 7, 20, 21, 29, 12, 28, 17,
            1, 15, 23, 26, 5, 18, 31, 10,
            2, 8, 24, 14, 32, 27, 3, 9,
            19, 13, 30, 6, 22, 11, 4, 25
    };

    final byte[] FP = {
            40, 8, 48, 16, 56, 24, 64, 32,
            39, 7, 47, 15, 55, 23, 63, 31,
            38, 6, 46, 14, 54, 22, 62, 30,
            37, 5, 45, 13, 53, 21, 61, 29,
            36, 4, 44, 12, 52, 20, 60, 28,
            35, 3, 43, 11, 51, 19, 59, 27,
            34, 2, 42, 10, 50, 18, 58, 26,
            33, 1, 41, 9, 49, 17, 57, 25
    };


    // ---------------------------------------------------------

    // Permuting the original message through IP (64-bit -> 64-bit)
    public BigInteger applyIP(BigInteger originalMsg) {
        BigInteger permuted = BigInteger.ZERO;

        for (int i = 0; i < IP.length; i++) {
            // pozycja bitu w oryginalnej wiadomosci (1-64)
            int originalBitPosition = IP[i];

            if (originalMsg.testBit(64 - originalBitPosition)) {
                permuted = permuted.setBit(63 - i);
            }
        }
//        logger.info("Wartość po IP: " + permuted.toString(16).toUpperCase());
        logger.info("Message after IP: " + permuted.toString(2) + " [OK]");
        return permuted;
    }

    public BigInteger applyPC1(BigInteger originalKey) {
        BigInteger permutedKey = BigInteger.ZERO;

        for (int i = 0; i < PC1.length; i++) {
            int bitPosition = PC1[i]; // pozycja bitu w oryginalnym kluczu (1-64)

            if (originalKey.testBit(64 - bitPosition)) {
                permutedKey = permutedKey.setBit(55 - i);
            }
        }
        return permutedKey;
    }

    public BigInteger applyPC2(BigInteger shiftedKey) {
        BigInteger permutedKey = BigInteger.ZERO;

        for (int i = 0; i < PC2.length; i++) {
            int bitPosition = PC2[i]; // pozycja bitu w kluczu po PC-1 (1-56)
            if (shiftedKey.testBit(56 - bitPosition)) {
                permutedKey = permutedKey.setBit(47 - i);
            }
        }
        return permutedKey;
    }

    // @num - Liczba do przesuniecia (C lub D)
    // @shifts - tablica przesuniec dla kazdej rundy
    // @round - numer aktualnej rundy (0-15)
    // @size - dlugosc bloku (28 dla C/D)
    public static BigInteger leftShift(BigInteger num, byte[] shifts, int round, int size) {
        int currentShift = shifts[round] & 0xFF; // konwersja byte do int (0-255)

        // maska dla size bitów (np. 0xFFFFFFF dla 28 bitów)
        BigInteger mask = BigInteger.ONE.shiftLeft(size).subtract(BigInteger.ONE); // maska nam zapewnia ze wynik ma 28 bitow

        // przesunięcie cykliczne w lewo
        BigInteger shifted = num.shiftLeft(currentShift)
                .or(num.shiftRight(size - currentShift)); // shiftRight - przenosi wypadajce bity na koniec. OR laczy wynik

        return shifted.and(mask);
    }

    // key scheduling
    public BigInteger[] generateRoundKeys(BigInteger originalKey) {
        BigInteger permutedKey = applyPC1(originalKey); // 56-bitowy klucz po PC-1

        logger.info("Key after PC-1: " + permutedKey.toString(2) + " [OK]");

        // przesuwamy bity w prawo o 28 bitow -> stosujemy maske wyciagajaca 28 bitow -> operacja AND zeruje wszystkie bity powyzej 28. pozycji
        BigInteger C0 = permutedKey.shiftRight(28).and(new BigInteger("FFFFFFF", 16));
        BigInteger D0 = permutedKey.and(new BigInteger("FFFFFFF", 16)); // ostatnie 28 bitow

        logger.info("C0: " + C0.toString(2) + " [OK]");
        logger.info("D0: " + D0.toString(2) + " [OK]");

        BigInteger[] roundKeys = new BigInteger[16]; // tablica na 16 podkluczy

        BigInteger C = C0;
        BigInteger D = D0;

        // "Left circular shift"
        for (int i = 0; i < 16; i++) {
            // przesuwamy bity w lewo (dla C i D)
            C = leftShift(C, SHIFTS, i, 28);
            D = leftShift(D, SHIFTS, i, 28);

            if (i == 0) {
                logger.info("C1: " + C.toString(2) + " [OK]");
                logger.info("D1: " + D.toString(2) + " [OK]");
            }

            if (i == 15) {
                logger.info("C16: " + C.toString(2) + " [OK]");
                logger.info("D16: " + D.toString(2) + " [OK]");
            }

            // po przesunieciu laczymy C i D (56 bitow) i wykonujemy permutacje PC-2
            BigInteger combined = C.shiftLeft(28).or(D);
            if (i == 0) {
                logger.info("CD1: " + combined.toString(2) + " [OK]");
            }
            if (i == 15) {
                logger.info("CD16: " + combined.toString(2) + " [OK]");
            }
            roundKeys[i] = applyPC2(combined);
        }
        logger.info("K1 after PC-2: " + roundKeys[0].toString(2) + " [OK]");
        logger.info("K15 after PC-2: " + roundKeys[14].toString(2) + " [OK]");
        logger.info("K16 after PC-2: " + roundKeys[15].toString(2) + " [OK]");

        return roundKeys;
    }


    private BigInteger feistelFunction(BigInteger R, BigInteger roundKey) {
        // rozszerzenie E (32-bit -> 48-bit)
        BigInteger expanded = expand(R);

        // XOR z kluczem rundy
        BigInteger xored = expanded.xor(roundKey);

        // podstawienie S-Box
        BigInteger substituted = substitute(xored);

        // permutacja P
        return applyP(substituted);
    }


    // rozszerzenie E (32-bit -> 48-bit)
    private BigInteger expand(BigInteger data) {
        BigInteger result = BigInteger.ZERO;
        for (int i = 0; i < E.length; i++) {
            if (data.testBit(32 - E[i])) {
                result = result.setBit(47 - i);
            }
        }
        return result;
    }


    // ta metoda dzieli 48 bitow na 8 grup po 6 bitow, i kazda grupe przepuszcza przez przez odpowiadajacego jej S-Boxa
    // z kazdej grupy 6-bitowej np. 'abcdef', 'af' oznacza wiersz S-Boxa, natomiast 'bcde' kolumne, otrzymana liczba
    // jest konwertowana na 4-bitowa reprezentacje tej liczby w komorce (np. jesli w komorce jest 6 to wynikiem bedzie 0110)
    // przyklad dla S-BOX1 mamy 101010, '10' oznacza wiersz = 2, '0101' kolumna = 5 => S1(101010) = 6 = 0110
    private BigInteger substitute(BigInteger data) {
        BigInteger result = BigInteger.ZERO;

        for (int i = 0; i < 8; i++) {
            // wyodrebniamy 6-bitowy blok
            int block = data.shiftRight(42 - i * 6).and(new BigInteger("3F", 16)).intValue();

            // pobieramy wiersz i kolumne
            int row = ((block & 0x20) >> 4) | (block & 0x01);
            int col = (block >> 1) & 0x0F;

            // tu zmienilem dla testu [i] -> [7-i]
            // pobieramy wartosc z S-Boxa
            int value = SBOX[i][row][col];

            // dodajemy do wyniku
            result = result.shiftLeft(4).or(BigInteger.valueOf(value));
        }
        return result;
    }


    // "Transposition"
    private BigInteger applyP(BigInteger data) {
        BigInteger result = BigInteger.ZERO;

        for (int i = 0; i < P.length; i++) {
            if (data.testBit(32 - P[i])) {
                result = result.setBit(31 - i);
            }
        }
        return result;
    }

    // "Final Permutation"
    private BigInteger applyFP(BigInteger data) {
        BigInteger result = BigInteger.ZERO;

        for (int i = 0; i < FP.length; i++) {
            if (data.testBit(64 - FP[i])) {
                result = result.setBit(63 - i);
            }
        }
        return result;
    }

    // potem dodac tu argument BigInteger originalMessage
    public BigInteger encode(BigInteger msg, BigInteger key) {
        BigInteger permutedMsg = applyIP(msg);

        // Dzielimy permutedMsg na pol (2 x 32-bit), otrzymujac L0 i R0
        BigInteger L0 = permutedMsg.shiftRight(32).and(new BigInteger("FFFFFFFF", 16));
        BigInteger R0 = permutedMsg.and(new BigInteger("FFFFFFFF", 16));

        logger.info("L0: " + L0.toString(2) + " [OK]");
        logger.info("R0: " + R0.toString(2) + " [OK]");

        // generujemy podklucze
        BigInteger[] roundKeys = generateRoundKeys(key);

        BigInteger L = L0;
        BigInteger R = R0;

        // 16 rund Feistela
        for (int i = 0; i < 16; i++) {
            BigInteger previousL = L;
            L = R;

            // funkcja Feistela (F) + XOR z lewa polowa
            R = previousL.xor(feistelFunction(R, roundKeys[i]));
            if (i == 0) {
                logger.info("R1: " + R.toString(2) + " [OK]");
                BigInteger Elog = expand(L); // expand(L) poniewaz chcemy wartosc E(R0) a teraz L = R0
                logger.info("E(R0): " + Elog.toString(2) + " [OK]");
                BigInteger xored = Elog.xor(roundKeys[i]);
                logger.info("XORED: " + xored.toString(2) + " [OK]");
                logger.info("S(): " + substitute(xored).toString(2) + " [OK]");
                logger.info("f1: " + feistelFunction(L, roundKeys[i]).toString(2) + " [OK]");
            }
        }

        // ostatnia zamiana po rundzie 16
        BigInteger combined = R.shiftLeft(32).or(L);

        // "Final Permutation" / "Inverse Initial Permutation"
        return applyFP(combined);
    }
}
