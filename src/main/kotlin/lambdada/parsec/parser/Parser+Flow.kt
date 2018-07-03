package lambdada.parsec.parser

import lambdada.parsec.io.CharReader
import lambdada.parsec.parser.Response.Accept
import lambdada.parsec.parser.Response.Reject

//
// Sequence
// NOTE: [do] comprehension should be better

infix fun <A, B> Parser<A>.then(p: Parser<B>): Parser<Pair<A, B>> = this flatMap { a -> p map { a to it } }

//
// Alternate Then
//

fun <A, B> Parser<Pair<A, B>>.first(): Parser<A> = this map { it.first }

infix fun <A, B> Parser<A>.thenLeft(p: Parser<B>): Parser<A> = (this then p).first()

fun <A, B> Parser<Pair<A, B>>.second(): Parser<B> = this map { it.second }

infix fun <A, B> Parser<A>.thenRight(p: Parser<B>): Parser<B> = (this then p).second()

//
// Choice
//

infix fun <A> Parser<A>.or(p: Parser<A>): Parser<A> = { reader ->
    val a = this(reader)
    when (a.consumed) {
        true -> a
        false -> a.fold({ a }, { p(reader) })
    }
}

//
// Kleene operator, optional
//

fun <A> opt(p: Parser<A>): Parser<A?> = p map { it as A? } or returns<A?>(null)

// NOTE: Greedy parsers | Prefix i.e. Function vs. Method

fun <A> optRep(p: Parser<A>): Parser<List<A>> = {
    // Fold not used / tailrec should be validated if terminal recursion can be detected only
    tailrec fun <A> occurrences(p: Parser<A>, acc: List<A>, consumed: Boolean, charReader: CharReader): Response<List<A>> {
        val a = p(charReader)
        return when (a) {
            is Reject -> Accept(acc, charReader, consumed)
            is Accept -> occurrences(p, acc + a.value, consumed || a.consumed, a.input)
        }
    }

    occurrences(p, listOf(), false, it)
}

fun <A> rep(p: Parser<A>): Parser<List<A>> = p then optRep(p) map { listOf(it.first) + it.second }

//
// End of stream
//

var eos: Parser<Unit> = {
    if (it.canRead()) {
        Reject(it.location(), false)
    } else {
        Accept(Unit, it, false)
    }
}