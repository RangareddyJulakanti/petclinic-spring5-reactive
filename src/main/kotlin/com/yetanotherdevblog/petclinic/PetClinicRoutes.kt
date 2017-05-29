package com.yetanotherdevblog.petclinic

import com.yetanotherdevblog.experimental.MyBlogPostHandler
import com.yetanotherdevblog.petclinic.handlers.OwnersHandler
import com.yetanotherdevblog.petclinic.handlers.PetTypeHandler
import com.yetanotherdevblog.petclinic.handlers.PetsHandler
import com.yetanotherdevblog.petclinic.handlers.SpecialitiesHandler
import com.yetanotherdevblog.petclinic.handlers.VetsHandler
import com.yetanotherdevblog.petclinic.handlers.VisitHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.DependsOn
import org.springframework.core.io.ClassPathResource
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.MediaType.TEXT_EVENT_STREAM
import org.springframework.http.MediaType.APPLICATION_JSON_UTF8
import org.springframework.web.reactive.function.server.RouterFunctions.resources
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono


@Configuration
class PetClinicRoutes() {

    @Bean
    @DependsOn("petClinicRouter")
    fun resourceRouter() = resources("/**", ClassPathResource("static/"))

    @Bean
    fun simpleRouter(blogHandler: MyBlogPostHandler) = router {
        "/posts".nest {
            GET("/{id}", blogHandler::getBlogPost)
            GET("/", blogHandler::getBlogPosts)
            (accept(APPLICATION_JSON) and (contentType(APPLICATION_JSON) or contentType(APPLICATION_JSON_UTF8))).nest {
                POST("/", blogHandler::save)
            }
        }
        GET("/test", { ok().body(Mono.just("test"), String::class.java) })
        accept(TEXT_EVENT_STREAM).nest { GET("/match", blogHandler::matchClock) }
    }

    @Bean
    fun petClinicRouter(ownersHandler: OwnersHandler,
                        specialitiesHandler: SpecialitiesHandler,
                        vetsHandler: VetsHandler,
                        petsHandler: PetsHandler,
                        petTypeHandler: PetTypeHandler,
                        visitHandler: VisitHandler) =
            router {
                GET("/", { ok().html().render("welcome") })
                "/owners".nest {
                    GET("/", ownersHandler::goToOwnersIndex)
                    "/add".nest {
                        GET("/", ownersHandler::goToAddPage)
                        POST("/", ownersHandler::add)
                    }
                    "/edit".nest {
                        GET("/", ownersHandler::goToEditPage)
                        POST("/", ownersHandler::edit)
                    }
                    "/view".nest {
                        GET("/", ownersHandler::view)
                    }
                }
                "/vets".nest {
                    GET("/", vetsHandler::goToVetsIndex)
                    "/add".nest {
                        GET("/", vetsHandler::goToAdd)
                        POST("/", vetsHandler::add)
                    }
                    "/edit".nest {
                        GET("/", vetsHandler::goToEdit)
                        POST("/", vetsHandler::edit)
                    }
                }
                "/pets".nest {
                    "/add".nest {
                        GET("/", petsHandler::goToAdd)
                        POST("/", petsHandler::add)
                    }
                    "/edit".nest {
                        GET("/", petsHandler::goToEdit)
                        POST("/", petsHandler::edit)
                    }
                }
                "/visits".nest {
                    "/add".nest {
                        GET("/", visitHandler::goToAdd)
                        POST("/", visitHandler::add)
                    }
                    "/edit".nest {
                        GET("/", visitHandler::goToEdit)
                        POST("/", visitHandler::edit)
                    }
                }
                "/specialities".nest {
                    GET("/", specialitiesHandler::goToSpecialitiesIndex)
                    "/add".nest {
                        GET("/", specialitiesHandler::goToAdd)
                        POST("/", specialitiesHandler::add)
                    }
                    "/edit".nest {
                        GET("/", specialitiesHandler::goToEdit)
                        POST("/", specialitiesHandler::edit)
                    }
                }
                "/petTypes".nest {
                    GET("/", petTypeHandler::goToPetTypeIndex)
                    "/add".nest {
                        GET("/", petTypeHandler::goToAdd)
                        POST("/", petTypeHandler::add)
                    }
                    "/edit".nest {
                        GET("/", petTypeHandler::goToEdit)
                        POST("/", petTypeHandler::edit)
                    }
                }

            }

}
