package io.github.bfarrell.springjpa

import static springfox.documentation.builders.PathSelectors.regex
import groovy.util.logging.Slf4j
import io.github.bfarrell.springjpa.model.Ingredient
import io.github.bfarrell.springjpa.model.Recipe
import io.github.bfarrell.springjpa.repository.RecipeRepository

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter

import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.service.ApiInfo
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2

@SpringBootApplication
@EnableSwagger2
@EnableScheduling
//@EnableAsync
@Slf4j
public class Application implements CommandLineRunner {
    
    @Autowired
    RecipeRepository recipeRepository
    
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args)
    }
    
    @Override
    public void run(String... args) {
        def recipe = new Recipe()
        recipe.setName('chicken picatta')
        def ingredientA = new Ingredient()
        ingredientA.setName('chicken')
        ingredientA.setRecipe(recipe)
        def ingredientB = new Ingredient()
        ingredientB.setName('capers')
        ingredientB.setRecipe(recipe)
        recipe.addIngredient(ingredientA)
        recipe.addIngredient(ingredientB)
        recipeRepository.save(recipe)

        def newRecipe = recipeRepository.findAll()
        System.out.println(newRecipe.toString())
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping('/v1/recipes').allowedOrigins('*')
                registry.addMapping('/v1/ingredients').allowedOrigins('*')
                registry.addMapping('/health').allowedOrigins('*')
            }
        }
    }

    @Bean
    Docket spinnakersApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .ignoredParameterTypes(MetaClass)
                .useDefaultResponseMessages(false)
                .groupName('springjpa')
                .apiInfo(apiInfo())
                .select()
                .paths(regex('/v1/.*'))
                .build()
    }

    ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title('Spring JPA Example Service REST')
                .description('Rest api for the Spring JPA Example Service')
                .version('1.0')
                .build()
    }
}
