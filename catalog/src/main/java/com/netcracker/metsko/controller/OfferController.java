package com.netcracker.metsko.controller;

import com.netcracker.metsko.entity.Category;
import com.netcracker.metsko.entity.ExceptionMessage;
import com.netcracker.metsko.entity.Offer;
import com.netcracker.metsko.entity.Price;
import com.netcracker.metsko.entity.dto.OfferDTO;
import com.netcracker.metsko.exceptions.NotCreatedException;
import com.netcracker.metsko.exceptions.NotDeletedException;
import com.netcracker.metsko.exceptions.NotFoundException;
import com.netcracker.metsko.exceptions.NotUpdatedException;
import com.netcracker.metsko.service.OfferService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/catalog/offers")
@Api(value = "Controller", description = "This is offer controller")
public class OfferController {

    @Autowired
    OfferService offerService;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping
    @ApiOperation(httpMethod = "POST",
            value = "Create an offer",
            response = Category.class,
            nickname = "createOffer")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Offer created"),
            @ApiResponse(code = 500, message = "Offer not created")
    })
    public ResponseEntity<String> createOffer(@Validated @RequestBody Offer newOffer) throws NotCreatedException, SQLException {

        if (newOffer.getName().length() != 0) {
            offerService.createOffer(newOffer);
            return new ResponseEntity<>("The offer is created", HttpStatus.CREATED);
        } else {
            throw new NotCreatedException(ExceptionMessage.NULL_FIELDS);
        }
    }

    @PutMapping
    @ApiOperation(httpMethod = "PUT",
            value = "Update offer",
            response = Category.class,
            nickname = "updateOffer")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Offer updated"),
            @ApiResponse(code = 404, message = "Offer not found"),
            @ApiResponse(code = 500, message = "Offer not updated")
    })
    public ResponseEntity<Offer> updateOffer(@RequestBody Offer offer) throws NotUpdatedException, SQLException {
        try {
            if (offer.getName().length() != 0 && offerService.findById(offer.getId()) != null) {
                Offer updatedOffer = offerService.updateOffer(offer);
                return new ResponseEntity<>(updatedOffer, HttpStatus.OK);
            } else {
                throw new NotUpdatedException(ExceptionMessage.NULL_FIELDS);
            }
        } catch (NotFoundException e) {
            throw new NotUpdatedException(ExceptionMessage.NOT_UPDATED);
        }
    }

    @DeleteMapping(value = "/{id}")
    @ApiOperation(httpMethod = "DELETE",
            value = "Delete an offer by id",
            response = Long.class,
            nickname = "deleteOffer")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Offer deleted"),
            @ApiResponse(code = 404, message = "Offer not found"),
            @ApiResponse(code = 500, message = "Error")
    })
    public ResponseEntity<String> deleteOffer(@PathVariable("id") Long id) throws NotDeletedException, SQLException {
        try {
            offerService.deleteOffer(id);
            return new ResponseEntity<>("The offer is deleted", HttpStatus.OK);
        } catch (Exception e) {
            throw new NotDeletedException(ExceptionMessage.NOT_DELETED);
        }

    }

    @PutMapping(value = "/{id}")
    @ApiOperation(httpMethod = "",
            value = "Set offer's availability",
            response = Long.class,
            nickname = "setAvailability"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Availability changed"),
            @ApiResponse(code = 404, message = "Offer not found"),
            @ApiResponse(code = 500, message = "Error")
    })
    public ResponseEntity<Void> setAvailability(@PathVariable("id") Long id, @RequestParam("availability") boolean availability) throws NotUpdatedException, SQLException {
        try {
            offerService.setAvailability(id, availability);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            throw new NotUpdatedException(ExceptionMessage.NOT_UPDATED);
        }
    }

    @GetMapping(value = "/{id}")
    @ApiOperation(httpMethod = "GET",
            value = "Find offers by id",
            response = Offer.class,
            nickname = "findById"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 302, message = "Offer found"),
            @ApiResponse(code = 404, message = "Offer not found"),
            @ApiResponse(code = 500, message = "Error")
    })
    public ResponseEntity<OfferDTO> findById(@PathVariable("id") Long id) throws NotFoundException, SQLException {
        Offer foundOffer = offerService.findById(id);
        OfferDTO offerDTO = modelMapper.map(foundOffer, OfferDTO.class);
        return new ResponseEntity<>(offerDTO, HttpStatus.OK);
    }

    @GetMapping
    @ApiOperation(httpMethod = "GET",
            value = "Find all offers",
            response = Offer.class,
            nickname = "findAll",
            responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 302, message = "Offers found"),
            @ApiResponse(code = 404, message = "Offers not found"),
            @ApiResponse(code = 500, message = "Error")
    })
    public ResponseEntity<List<Offer>> findAll() throws NotFoundException, SQLException {
        List<Offer> offerList = offerService.findAll();
        return new ResponseEntity<>(offerList, HttpStatus.OK);
    }

    @GetMapping(value = "/searchbytags")
    @ApiOperation(httpMethod = "GET",
            value = "Find offers by tags",
            response = Offer.class,
            nickname = "findByTags",
            responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 302, message = "Offers found"),
            @ApiResponse(code = 404, message = "Offers not found"),
            @ApiResponse(code = 500, message = "Error")
    })
    public ResponseEntity<List<Offer>> findByTags(@RequestParam String tagList) throws NotFoundException, SQLException {
        List<Offer> list = offerService.findByTags(tagList);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping(value = "/availability")
    @ApiOperation(httpMethod = "GET",
            value = "Find (un)available offers",
            response = Offer.class,
            nickname = "findOffersByAvailability",
            responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 302, message = "Offers found"),
            @ApiResponse(code = 404, message = "Offers not found"),
            @ApiResponse(code = 500, message = "Error")
    })
    public ResponseEntity<List<Offer>> findOffersByAvailability(@RequestParam("availability") boolean availability) throws NotFoundException, SQLException {
        List<Offer> offerList = offerService.findOffersByAvailability(availability);
        return new ResponseEntity<>(offerList, HttpStatus.OK);
    }

    @PutMapping(value = "/{id}/prices")
    @ApiOperation(httpMethod = "PUT",
            value = "Add price to offer",
            response = Long.class,
            nickname = "addPrice")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Price added"),
            @ApiResponse(code = 404, message = "Offer not found"),
            @ApiResponse(code = 500, message = "Error")
    })
    public ResponseEntity<String> addPrice(@PathVariable("id") Long id, @RequestBody Price price) throws NotUpdatedException, SQLException {
        try {
            offerService.addPrice(id, price);
            return new ResponseEntity<>("The price is added.", HttpStatus.OK);
        } catch (Exception e) {

            throw new NotUpdatedException(ExceptionMessage.NOT_ADDED);
        }
    }

    @PutMapping(value = "/{id}/changeprices")
    @ApiOperation(httpMethod = "PUT",
            value = "Change offer's price",
            response = Category.class,
            nickname = "changePrice")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Price updated"),
            @ApiResponse(code = 404, message = "Offer not found"),
            @ApiResponse(code = 500, message = "Error")
    })
    public ResponseEntity<String> changePrice(@PathVariable("id") Long id, @RequestParam Double price) throws NotUpdatedException, SQLException {
        try {
            offerService.changePrice(id, price);
            return new ResponseEntity<>("The price is changed.", HttpStatus.OK);
        } catch (Exception e) {
            throw new NotUpdatedException(ExceptionMessage.NOT_UPDATED);
        }
    }

    @GetMapping(value = "/pricefilters")
    @ApiOperation(httpMethod = "GET",
            value = "Find offers between two prices",
            response = Offer.class,
            nickname = "getOfferByPriceFromTo",
            responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 302, message = "Offers found"),
            @ApiResponse(code = 404, message = "Offers not found"),
            @ApiResponse(code = 500, message = "Error")
    })
    public ResponseEntity<List<Offer>> getOfferByPriceFromTo(@RequestParam("min") double priceFrom, @RequestParam("max") double priceTo) throws NotFoundException, SQLException {
        try {
            List<Offer> offerList = offerService.getPriceFromTo(priceFrom, priceTo);
            return new ResponseEntity<>(offerList, HttpStatus.OK);

        } catch (Exception e) {
            throw new NotFoundException(ExceptionMessage.NOT_FOUND);
        }
    }

    @PutMapping(value = "/{id}/tags")
    @ApiOperation(httpMethod = "PUT",
            value = "Add a tag to offer",
            response = Long.class,
            nickname = "addTag")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Tag added"),
            @ApiResponse(code = 404, message = "Offer not found"),
            @ApiResponse(code = 500, message = "Error")
    })
    public ResponseEntity<String> addTag(@PathVariable("id") Long id, @RequestParam Long tagId) throws NotUpdatedException, SQLException {
        try {
            offerService.addTag(id, tagId);
            return new ResponseEntity<>("The tag is added.",HttpStatus.OK);
        } catch (Exception e) {
            throw new NotUpdatedException(ExceptionMessage.NOT_ADDED);
        }

    }

    @PutMapping(value = "/{id}/removetags")
    @ApiOperation(httpMethod = "PUT",
            value = "Remove a tag from offer",
            response = Long.class,
            nickname = "removeTag")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Tag removed"),
            @ApiResponse(code = 404, message = "Tag not removed"),
            @ApiResponse(code = 500, message = "Error")
    })
    public ResponseEntity<String> removeTag(@PathVariable("id") Long id, @RequestParam("tagId") Long tagId) throws NotUpdatedException, SQLException, NotDeletedException {
        try {
            offerService.removeTag(id, tagId);
            return new ResponseEntity<>("The tag is removed.", HttpStatus.OK);
        } catch (Exception e) {
            throw new NotDeletedException(ExceptionMessage.NOT_DELETED);
        }

    }

    @PutMapping(value = "/{id}/categories")
    @ApiOperation(httpMethod = "PUT",
            value = "Add a category to offer",
            response = Long.class,
            nickname = "addCategory")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Category added"),
            @ApiResponse(code = 404, message = "Offer not found"),
            @ApiResponse(code = 500, message = "Error")
    })
    public ResponseEntity<String> addCategory(@PathVariable("id") Long id, @RequestParam Long categoryId) throws NotUpdatedException, SQLException {
        try {
            offerService.addCategory(id, categoryId);
            return new ResponseEntity<>("The category is added.", HttpStatus.OK);
        } catch (Exception e) {
            throw new NotUpdatedException(ExceptionMessage.NOT_ADDED);
        }
    }

    @PutMapping(value = "/{id}/removecategories")
    @ApiOperation(httpMethod = "PUT",
            value = "Remove a category from offer",
            response = Long.class,
            nickname = "removeCategory")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Category removed"),
            @ApiResponse(code = 404, message = "Category not removed"),
            @ApiResponse(code = 500, message = "Error")
    })
    public ResponseEntity<String> removeCategory(@PathVariable("id") Long id) throws NotDeletedException, SQLException, NotUpdatedException {
        try {
            offerService.removeCategory(id);
            return new ResponseEntity<>("The category is removed.", HttpStatus.OK);
        } catch (Exception e) {
            throw new NotDeletedException(ExceptionMessage.NOT_DELETED);
        }
    }


    @PostMapping(value = "/categories/offers/filteredOffers")
    ResponseEntity<List<OfferDTO>> findFilteredOffers(@RequestBody Map<String, String> offerFilter) throws SQLException, NotFoundException {
        try {
            List<Offer> offers = offerService.findFilteredOffers(offerFilter);
            List<OfferDTO> offersDTO = offers.stream().map(offer -> modelMapper.map(offer, OfferDTO.class)).collect(Collectors.toList());
            return new ResponseEntity<>(offersDTO, HttpStatus.FOUND);
        } catch (Exception e) {
            throw new NotFoundException(ExceptionMessage.NOT_FOUND);
        }
    }

}
