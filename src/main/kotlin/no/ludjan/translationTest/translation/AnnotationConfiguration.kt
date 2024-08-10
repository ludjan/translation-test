package no.ludjan.translationTest.translation

import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Configuration
import org.springframework.web.bind.annotation.RestController
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

@Configuration
class AnnotationConfiguration {

    @Autowired
    lateinit var applicationContext: ApplicationContext

    @PostConstruct
    fun processAnnotations() {
        applicationContext
            .getBeansOfType(Any::class.java)
            .forEach { (_, beanInstance) ->
                beanInstance::class
                    .memberProperties
                    .forEach { property ->

                        property
                            .findAnnotation<ExtractForTranslation>()
                            ?.let { annotationInstance ->

                                val value = getValueForKey(
                                    annotationInstance.key,
                                    annotationInstance.defaultValue
                                )

                                property.isAccessible = true
                                if (property is KMutableProperty<*>) {
                                    property.setter.call(beanInstance, value)
                                } else throw IllegalArgumentException("Property is not mutable or not a valid property.")
                            }
                    }
            }
    }

    private fun getValueForKey(key: String, defaultValue: String): String {
        val language = "no"
        return TranslationLookuper.lookup(key, language) ?: defaultValue
    }
}