package io.github.bfarrell.springjpa.repository

import io.github.bfarrell.springjpa.model.Recipe

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface RecipeRepository extends CrudRepository<Recipe, String> {
    
//    List<Vulnerability> findByVulnerableProductsContaining(String filter)

}
