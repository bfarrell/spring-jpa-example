package io.github.bfarrell.springjpa.model

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
class Ingredient {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    long id
    String name
    
    @ManyToOne
    @JoinColumn(name = "recipe_name")
    // Use the following annotation to resolve infinite recursion
    @JsonBackReference
    Recipe recipe    
    
    long getId() {
        return id
    }
    void setId(long id) {
        this.id = id
    }
    
    String getName() {
        return name
    }
    void setName(String name) {
        this.name = name
    }
    
    String getRecipe() {
        return recipe
    }
    void setRecipe(Recipe recipe) {
        this.recipe = recipe
    }

}
