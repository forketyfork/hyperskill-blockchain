package blockchain

import java.security.MessageDigest

fun applySha256(input: String) = MessageDigest.getInstance("SHA-256")
    .digest(input.toByteArray(charset("UTF-8")))
    .map(Byte::toInt).joinToString("") {
        Integer.toHexString(0xff and it).padStart(2, '0')
    }
