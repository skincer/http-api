package com.sck.web;

import com.sck.domain.RestInitializable;
import com.sck.repository.JpaSpecRepository;
import com.sck.transfer.PaginatedDTO;
import com.sck.utility.rsql.RsqlVisitor;
import com.sck.utility.web.ControllerUtility;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by TEKKINCERS on 7/8/2015.
 *
 * Generic REST Controller to be extended by specific controllers
 */
public abstract class BaseRestController<T, ID extends Serializable> {

    protected JpaSpecRepository<T, ID> repository;

    public BaseRestController(JpaSpecRepository<T, ID> repository) {
        this.repository = repository;
    }

    /*
    GET all records - Paginated
     */
    @RequestMapping(
            value = "",
            method = RequestMethod.GET,
            produces = {"application/json","application/xml"}
    )
    public ResponseEntity<PaginatedDTO<T>> getAll(Pageable pageable) {

        Page<T> foundObjects = repository.findAll(pageable);

        return Optional.ofNullable(foundObjects)
                .map(objects -> new ResponseEntity<>(
                        new PaginatedDTO<T>(objects.getContent(), objects.isLast(), objects.isFirst(), objects.getTotalElements(), objects.getTotalPages(), objects.getSize(), objects.getNumber(), objects.getNumberOfElements(), objects.getSort()),
                        HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /*
    GET a single record
    */
    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.GET,
            produces = {"application/json","application/xml"}
    )
    public ResponseEntity<T> get(@PathVariable ID id) {

        T foundObject = repository.findOne(id);

        if(foundObject == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(foundObject, HttpStatus.OK);
    }

    /*
    POST create one record
     */
    @RequestMapping(
            value = "",
            method = RequestMethod.POST,
            consumes = {"application/json","application/xml"},
            produces = {"application/json","application/xml"}
    )
    public ResponseEntity<?> create(@Valid @RequestBody T postedObject) {

        if(postedObject instanceof RestInitializable) {
            if(((RestInitializable) postedObject).getId() != null) {
                return ResponseEntity.badRequest().body("A new record cannot have an ID");
            }

            ControllerUtility.foreignKeyToEntityTranslation((RestInitializable)postedObject);

            return Optional.ofNullable(repository.save(postedObject))
                    .map(gate1 -> new ResponseEntity<>(
                            gate1,
                            HttpStatus.CREATED))
                    .orElse(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
        }

        return ResponseEntity.badRequest().body("Entity not configured for REST");
    }

    /*
    POST create all posted records
     */
    @RequestMapping(
            value = "/array",
            method = RequestMethod.POST,
            consumes = {"application/json","application/xml"},
            produces = {"application/json","application/xml"}
    )
    public ResponseEntity<?> createAll(@Valid @RequestBody List<T> postedObjects) {

        for(T postedObject : postedObjects) {
            if(postedObject instanceof RestInitializable) {
                if(((RestInitializable) postedObject).getId() != null) {
                    return ResponseEntity.badRequest().body("A new record cannot have an ID");
                }

                ControllerUtility.foreignKeyToEntityTranslation((RestInitializable)postedObject);

            }
        }

        return Optional.ofNullable(repository.save(postedObjects))
                .map(gate1 -> new ResponseEntity<>(
                        gate1,
                        HttpStatus.CREATED))
                .orElse(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    /*
    PUT update one record
     */
    @RequestMapping(
            value = "",
            method = RequestMethod.PUT,
            consumes = {"application/json","application/xml"},
            produces = {"application/json","application/xml"}
    )
    public ResponseEntity<?> update(@Valid @RequestBody T postedObject) {

        T existingObject = null;

        if(postedObject instanceof RestInitializable) {
            if(((RestInitializable) postedObject).getId() == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }else {
                existingObject = repository.findOne((ID)((RestInitializable) postedObject).getId());
            }
        }else {
            return new ResponseEntity<>("Entity not configured for updates", HttpStatus.NOT_IMPLEMENTED);
        }

        if(existingObject == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        ControllerUtility.foreignKeyToEntityTranslation((RestInitializable)postedObject);

        ControllerUtility.copyProperties(postedObject, existingObject, true);


        return Optional.ofNullable(repository.save(existingObject))
                .map(object -> new ResponseEntity<>(
                        object,
                        HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    /*
    PUT update many records
     */
    @RequestMapping(
            value = "/array",
            method = RequestMethod.PUT,
            consumes = {"application/json","application/xml"},
            produces = {"application/json","application/xml"}
    )
    public ResponseEntity<?> updateAll(@Valid @RequestBody List<T> postedObjects) {

        List<T> existingObjects = new ArrayList<>();

        for(T postedObject : postedObjects) {

            T existingObject = null;

            if(postedObject instanceof RestInitializable) {
                if(((RestInitializable) postedObject).getId() == null) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }else {
                    existingObject = repository.findOne((ID)((RestInitializable) postedObject).getId());
                }
            }else {
                return new ResponseEntity<>("Entity not configured for updates", HttpStatus.NOT_IMPLEMENTED);
            }

            if(existingObject == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            ControllerUtility.foreignKeyToEntityTranslation((RestInitializable)postedObject);

            ControllerUtility.copyProperties(postedObject, existingObject, true);

            existingObjects.add(existingObject);
        }

        return Optional.ofNullable(repository.save(existingObjects))
                .map(object -> new ResponseEntity<>(
                        object,
                        HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    /*
    DELETE a single record
     */
    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.DELETE
    )
    public ResponseEntity<?> delete(@PathVariable ID id) {

        repository.delete(id);

        if(repository.findOne(id) == null) return new ResponseEntity<>(HttpStatus.OK);

        return new ResponseEntity<>("Could not delete record: "+id, HttpStatus.NOT_ACCEPTABLE);
    }

    /*
    DELETE all posted records
     */
    @RequestMapping(
            value = "/array",
            method = RequestMethod.DELETE,
            consumes = {"application/json","application/xml"}
    )
    public ResponseEntity<?> deleteAll(@Valid @RequestBody List<T> postedObjects) {

        repository.delete(postedObjects);

        return ResponseEntity.ok().build();
    }

    /*
    DELETE all posted records without a body
     */
    @RequestMapping(
            value = "",
            method = RequestMethod.DELETE
    )
    public ResponseEntity<?> deleteAllId(@RequestParam(value = "ids") String ids) {

        String[] idArray = ids.split("\\|");
        for(String id : idArray) {
            if(!id.isEmpty()) {
                try {
                    repository.delete((ID)Long.valueOf(id));
                }catch (EmptyResultDataAccessException e) {
                    // long hair, don't care
                }
            }
        }

        return ResponseEntity.ok().build();
    }


    @RequestMapping(
            value = "/search",
            method = RequestMethod.GET,
            produces = {"application/json","application/xml"}
    )
    public ResponseEntity<?> search(
            @RequestParam(value = "query") String query,
            Pageable pageable
    ) {

        Node rootNode = new RSQLParser().parse(query);

        Specification<T> specification = rootNode.accept(new RsqlVisitor<>());

        Page<T> page;

        try {
            page = repository.findAll(specification, pageable);
        }catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Check your field names");
        }

        if(page == null) {
            return ResponseEntity.badRequest().build();
        }

        if(page.hasContent()) {
            return ResponseEntity.ok(
                    new PaginatedDTO<>(page.getContent(), page.isLast(), page.isFirst(), page.getTotalElements(), page.getTotalPages(), page.getSize(), page.getNumber(), page.getNumberOfElements(), page.getSort())
                    );
        }
        return ResponseEntity.notFound().build();
    }

}
