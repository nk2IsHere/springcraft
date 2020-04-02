package eu.nk2.springcraft.utils

import reactor.core.publisher.Mono

fun <T> Mono<T>.errorIfEmpty(throwable: Throwable): Mono<T> =
    this.switchIfEmpty(Mono.defer { Mono.error<T>(throwable) })
