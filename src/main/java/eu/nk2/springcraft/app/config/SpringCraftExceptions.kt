package eu.nk2.springcraft.app.config

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.server.ResponseStatusException

@ResponseStatus(value = HttpStatus.NOT_FOUND)
class ResourceNotFoundException(type: String, key: String, value: Any?): ResponseStatusException(HttpStatus.NOT_FOUND, "Required $key of $type not found: $value")

@ResponseStatus(value = HttpStatus.CONFLICT)
class ResourceDuplicateException(type: String, key: String, value: Any?): ResponseStatusException(HttpStatus.CONFLICT, "$key of $type is already exists: $value")

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
class ResourceTypeNotValidException(type: String, requestedType: String, requiredType: String): ResponseStatusException(HttpStatus.BAD_REQUEST, "Operation is permitted for $type $requestedType, $requiredType is required")

@ResponseStatus(value = HttpStatus.FORBIDDEN)
class ResourceDataMismatchException(type: String, key: String, originalValue: Any?, value: Any?): ResponseStatusException(HttpStatus.FORBIDDEN, "Required $key of $type is wrong: $value was provided instead of $originalValue")
