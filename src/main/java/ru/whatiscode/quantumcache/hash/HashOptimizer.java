package ru.whatiscode.quantumcache.hash;

import lombok.NonNull;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Singleton;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.CRC32;

@Slf4j
@Singleton
@Log4j
@Log
public class HashOptimizer {

    private static final int LINEAR_PROBING_LIMIT = 10;
    private static final double GOLDEN_RATIO_CONSTANT = (Math.sqrt(5) - 1) / 2;

    public int generateOptimalHash(Object key,
                                   int tableSize) {
        int primaryHash = calculateEnhancedHash(key);

        return doubleHashing(primaryHash, tableSize);
    }

    private int calculateEnhancedHash(Object key) {
        if (key == null) return 0;

        String keyString = key.toString();
        int baseHash = keyString.hashCode();

        int crcHash = calculateCrc32Hash(keyString);
        int md5Hash = calculateMD5Hah(keyString);

        return mixHashes(baseHash, crcHash, md5Hash);
    }

    private int calculateMD5Hah(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            return bytesToInt(digest);
        } catch (NoSuchAlgorithmException e) {
            log.warning("MD5 algorithm not available, using fallback");

            return input.hashCode();
        }
    }

    private int bytesToInt(byte[] bytes) {
        int result = 0;

        for (int i = 0; i < Math.min(4, bytes.length); i ++) {
            result = (result << 8) | (bytes[i] & 0xFF);
        }

        return result;
    }

    private int calculateCrc32Hash(String input) {
        CRC32 crc32 = new CRC32();

        crc32.update(input.getBytes(StandardCharsets.UTF_8));
        return (int) crc32.getValue();
    }

    private int mixHashes(int h1,
                          int h2,
                          int h3) {
        int a = h1 ^ h3;
        int b = h2 ^ (h1 >>> 16);
        int c = h3 ^ (h2 << 16);

        a = a ^ (a >>> 16);
        b = b ^ (b >>> 16);
        c = c ^ (c >>> 16);

        return a ^ b ^ c;
    }

    private int doubleHashing(int primaryHash,
                              int tableSize) {
        if (tableSize <= 1) return 0;

        int h1 = Math.abs(primaryHash % tableSize);

        int h2 = 1 + (Math.abs(primaryHash) % (tableSize - 1));

        double goldenHash = (h1 * GOLDEN_RATIO_CONSTANT) % 1;
        int goldenIndex = (int) (tableSize * goldenHash);

        return (h1 + h2 * goldenIndex % tableSize);
    }

    public int probeSequence(int hash,
                             int attempt,
                             int tableSize) {
        return (hash + attempt * attempt) % tableSize;
    }

}
