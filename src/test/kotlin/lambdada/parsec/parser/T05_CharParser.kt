package lambdada.parsec.parser

import lambdada.parsec.io.Reader
import org.junit.Assert.assertEquals
import org.junit.Test

class T05_CharParser {

    @Test
    fun shouldCharParserReturnsAccept() {
        val parser = char('a')

        val result = parser.invoke(Reader.string("a")).fold({ it.value == 'a' }, { false })

        assertEquals(result, true)
    }

    @Test
    fun shouldCharParserReturnsFails() {
        val parser = char('a')

        val result = parser.invoke(Reader.string("b")).fold({ false }, { true })

        assertEquals(result, true)
    }

    @Test
    fun shouldCharInRangeParserReturnsAccept() {
        val parser = charIn('a'..'z')

        val result = parser.invoke(Reader.string("a")).fold({ it.value == 'a' }, { false })

        assertEquals(result, true)
    }

    @Test
    fun shouldCharInRangeParserReturnsFails() {
        val parser = charIn('b'..'z')

        val result = parser.invoke(Reader.string("a")).fold({ false }, { true })

        assertEquals(result, true)
    }

    @Test
    fun shouldCharInStringParserReturnsAccept() {
        val parser = charIn("-+")

        val result = parser.invoke(Reader.string("-")).fold({ it.value == '-' }, { false })

        assertEquals(result, true)
    }

    @Test
    fun shouldCharInStringParserReturnsFails() {
        val parser = charIn("-+")

        val result = parser.invoke(Reader.string("/")).fold({ false }, { true })

        assertEquals(result, true)
    }


    @Test
    fun shouldCharParserOrParserReturnsAccept() {
        val parser = char('a') or char('b')

        val result = parser.invoke(Reader.string("b")).fold({ it.value == 'b' }, { false })

        assertEquals(result, true)
    }

}