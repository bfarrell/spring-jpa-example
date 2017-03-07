package io.github.bfarrell.springjpa.model

import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.OneToMany

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
class Recipe {
    @Id
    String name
    
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "recipe")
    List<Ingredient> ingredients
    
    String getName() {
        return name
    }
    void setName(String name) {
        this.name = name
    }
    
    List<Ingredient> getIngredients() {
        return ingredients
    }
    void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients
    }
    void addIngredient(Ingredient ingredient) {
        if (!ingredients) {
            ingredients = new ArrayList<Ingredient>()
        }
        ingredients.add(ingredient)
    }
    
    @JsonIgnore
    boolean isValid() {
        return name?.trim()
    }
}
