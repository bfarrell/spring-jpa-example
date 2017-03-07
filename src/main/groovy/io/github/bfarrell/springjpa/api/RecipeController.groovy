package io.github.bfarrell.springjpa.api

import groovy.util.logging.Slf4j
import io.github.bfarrell.springjpa.model.Recipe
import io.github.bfarrell.springjpa.repository.RecipeRepository
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Api(value = 'recipes')
@RestController
@RequestMapping(value = '/v1/recipes', produces = 'application/json')
@Slf4j
class RecipeController {
    @Autowired
    RecipeRepository repo

    @ApiOperation(value = 'add recipes', notes = 'add recipe', response = Object.class)
    @ApiResponses(value = [
        @ApiResponse(code = 200, message = 'recipe added', response = Recipe.class),
        @ApiResponse(code = 405, message = 'invalid input', response = Error.class),
        @ApiResponse(code = 500, message = 'unexpected error', response = Error.class)])
    @RequestMapping(value = '', produces = ['application/json'], consumes = ['application/json'],
    method = RequestMethod.POST)
    ResponseEntity<Object> addRecipe(@ApiParam(value = 'recipe object'  ) @RequestBody Recipe body) {
        def response = null
        if (!body.isValid()) {
            def error = 'Missing require attributes during recipe add'
            log.error(error)
            def err = new Error(HttpStatus.BAD_REQUEST.value, null, error)
            response = new ResponseEntity<Error>(err, HttpStatus.BAD_REQUEST)
        } else {
            try {
                def obj = repo.findOne(body.getName())
                // if existing with that name
                if (obj) {
                    def error = "Error adding $body.name as it already exists"
                    log.error(error)
                    def err = new Error(HttpStatus.CONFLICT.value, null, error)
                    response = new ResponseEntity<Error>(err, HttpStatus.CONFLICT)
                } else {
                    repo.save(body)
                    obj = repo.findOne(body.getName())
                    response = new ResponseEntity<Recipe>(obj, HttpStatus.CREATED)
                    log.info("Added new recipe $body.name")
                }
            } catch (Exception e) {
                def error = 'Unexpected error during adding of recipe'
                log.error(error)
                def err = new Error(HttpStatus.INTERNAL_SERVER_ERROR.value, e, error)
                response = new ResponseEntity<Error>(err, HttpStatus.INTERNAL_SERVER_ERROR)
            }
        }
        return response
    }

    @ApiOperation(value = 'get recipes', notes = 'get recipes ', response = Recipe.class, responseContainer = 'List')
    @ApiResponses(value = [
        @ApiResponse(code = 200, message = 'an array of recipes', response = Recipe.class),
        @ApiResponse(code = 500, message = 'unexpected error', response = Error.class)])
    @RequestMapping(value = '',
    produces = ['application/json'], method = RequestMethod.GET)
    ResponseEntity<Object> getrecipes(@ApiParam(value = 'filter of the recipe rpoducts')
            @RequestParam(value = 'filter', required = false) String filter) {
        def response = null
        try {
            def objs = null
            // TODO : Handle filters
            objs = repo.findAll()
            response = new ResponseEntity<List<Recipe>>(objs, HttpStatus.OK)
            log.info("Get all recipes")
        } catch (Exception e) {
            def error = 'Unexpected error during single get of recipe'
            log.error(error)
            def err = new Error(HttpStatus.INTERNAL_SERVER_ERROR.value, e, error)
            response = new ResponseEntity<Error>(err, HttpStatus.INTERNAL_SERVER_ERROR)
        }
        return response
    }

    @ApiOperation(value = 'find recipe by name', notes = 'returns a recipe by name', response = Recipe.class)
    @ApiResponses(value = [
        @ApiResponse(code = 200, message = 'successful operation', response = Recipe.class),
        @ApiResponse(code = 400, message = 'invalid name supplied', response = Error.class),
        @ApiResponse(code = 404, message = 'recipe not found', response = Error.class),
        @ApiResponse(code = 500, message = 'unexpected error', response = Error.class)])
    @RequestMapping(value = '/{recipeName}', produces = [ 'application/json' ], method = RequestMethod.GET)
    ResponseEntity<Recipe> getRecipeByName(@ApiParam(value = 'name of recipe',required=true)
            @PathVariable('recipeName') String recipeName) {
        def response = null
        try {
            def obj = repo.findOne(recipeName)
            if (!obj) {
                def error = 'Cannot find recipe to get'
                log.error(error)
                def err = new Error(HttpStatus.NOT_FOUND.value, null, error)
                response = new ResponseEntity<Error>(err, HttpStatus.NOT_FOUND)
            } else {
                response = new ResponseEntity<Recipe>(obj, HttpStatus.OK)
            }
        } catch (Exception e) {
            def error = 'Unexpected error during single get of recipe'
            log.error(error)
            def err = new Error(HttpStatus.INTERNAL_SERVER_ERROR.value, e, error)
            response = new ResponseEntity<Error>(err, HttpStatus.INTERNAL_SERVER_ERROR)
        }
        return response
    }

    @ApiOperation(value = 'update recipes', notes = 'update a recipe. ', response = Recipe.class)
    @ApiResponses(value = [
        @ApiResponse(code = 200, message = 'recipe added', response = Recipe.class),
        @ApiResponse(code = 400, message = 'invalid vulnerabilty name', response = Error.class),
        @ApiResponse(code = 405, message = 'invalid input', response = Error.class),
        @ApiResponse(code = 500, message = 'unexpected error', response = Error.class)])
    @RequestMapping(value = '/{recipeName}', produces = ['application/json'],
    consumes = ['application/json'], method = RequestMethod.PUT)
    ResponseEntity<Object> updateRecipe(@ApiParam(value = 'name of recipe',required=true )
            @PathVariable('recipeName') String recipeName, @ApiParam(value = 'recipe object') @RequestBody Recipe body) {
        def response = null
        if (!body.isValid()) {
            def error = 'Missing require attributes during recipe update'
            log.error(error)
            def err = new Error(HttpStatus.BAD_REQUEST.value, null, error)
            response = new ResponseEntity<Error>(err, HttpStatus.BAD_REQUEST)
        } else {
            try {
                def obj = repo.findOne(body.getName())
                if (!obj) {
                    def error = 'Cannot find recipe to update'
                    log.error(error)
                    def err = new Error(HttpStatus.NOT_FOUND.value, null, error)
                    response = new ResponseEntity<Error>(err, HttpStatus.NOT_FOUND)
                } else {
                    if (!body.getName() || !body.getName().equals(recipeName)) {
                        body.setName(recipeName)
                    }
                    repo.save(body)
                    obj = repo.findOne(recipeName)
                    response = new ResponseEntity<Recipe>(obj, HttpStatus.OK)
                    log.info("Recipe $recipeName updated")
                }
            } catch (Exception e) {
                def error = "Unexpected error during update of recipe $recipeName"
                log.error(error)
                def err = new Error(HttpStatus.INTERNAL_SERVER_ERROR.value, e, error)
                response = new ResponseEntity<Error>(err, HttpStatus.INTERNAL_SERVER_ERROR)
            }
        }
        return response
    }


    @ApiOperation(value = 'remove a recipe', notes = 'remove a recipe', response = Void.class)
    @ApiResponses(value = [@ApiResponse(code = 400, message = 'invalid vulnerabilty name', response = Void.class)])
    @RequestMapping(value = '/{recipeName}',
    produces = [ 'application/json' ], method = RequestMethod.DELETE)
    ResponseEntity<Object> removeRecipe(@ApiParam(value = 'recipe to delete', required=true)
            @PathVariable('recipeName') String recipeName) {
        def response = null
        try {
            repo.delete(recipeName)
            response = new ResponseEntity<Void>(HttpStatus.OK)
            log.info("$recipeName deleted.")
        } catch (Exception e) {
            def error = 'Error during delete'
            def err = new Error(HttpStatus.INTERNAL_SERVER_ERROR.value, e, error)
            log.error(error, e)
            response = new ResponseEntity<Error>(err, HttpStatus.INTERNAL_SERVER_ERROR)
        }
        return response
    }
}
