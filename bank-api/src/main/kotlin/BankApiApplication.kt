package kr.co.won.bank;


import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["kr.co.won.bank"])
class BankApiApplication

fun main(args: Array<String>) {
    runApplication<BankApiApplication>(*args)
}
