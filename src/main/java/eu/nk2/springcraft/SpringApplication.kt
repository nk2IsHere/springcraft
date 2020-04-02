package eu.nk2.springcraft

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.runApplication

@SpringBootApplication(exclude = [
    SecurityAutoConfiguration::class,
    JacksonAutoConfiguration::class
])
class SpringCraftApplication

fun runSpringApplication() {
    SpringApplication.run(SpringCraftApplication::class.java)
}
