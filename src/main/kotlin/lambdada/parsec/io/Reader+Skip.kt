package lambdada.parsec.io

import lambdada.parsec.io.ReaderWithSkip.Companion.skipNow
import lambdada.parsec.parser.Parser

private class ReaderWithSkip<A>(private val skip: Parser<A, *>, private val reader: Reader<A>) : Reader<A> {

    override fun location() = reader.location()
    override fun read() = reader.read()?.let { it.first to skipNow(skip, it.second) }

    companion object {
        internal fun <A> skipNow(skip: Parser<A, *>, charReader: Reader<A>): ReaderWithSkip<A> =
                ReaderWithSkip(skip, skip.invoke(charReader).fold({ it.input }, { charReader }))
    }

}

infix fun <A> Reader<A>.skip(skip: Parser<A, *>): Reader<A> = skipNow(skip, this)
