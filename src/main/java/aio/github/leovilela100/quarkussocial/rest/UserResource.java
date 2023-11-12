package aio.github.leovilela100.quarkussocial.rest;

import aio.github.leovilela100.quarkussocial.domain.model.User;
import aio.github.leovilela100.quarkussocial.domain.repository.UserRepository;
import aio.github.leovilela100.quarkussocial.rest.dto.CreateUserRequest;
import aio.github.leovilela100.quarkussocial.rest.dto.ResponseError;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.h2.command.ddl.CreateUser;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

import javax.swing.*;
import java.util.Set;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    private final UserRepository userRepository;
    private final Validator validator;

    @Inject
    public UserResource(UserRepository userRepository, Validator validator) {
        this.userRepository = userRepository;
        this.validator = validator;
    }

    @GET
    public Response listAllUser() {
        PanacheQuery<User> query = userRepository.findAll();
        return Response.ok(query.list()).build();
    }

    @POST
    @Transactional
    public Response createUser( CreateUserRequest userRequest ) {


        Set<ConstraintViolation<CreateUserRequest>> vaiolations = validator.validate(userRequest);

        if(!vaiolations.isEmpty()) {

            return ResponseError
                    .createFromValidator(vaiolations)
                    .withStatusCode(ResponseError.UNPROCESSABLE_ENTITY_STATUS);
        }

        User user = new User();

        user.setAge(userRequest.getAge());
        user.setName(userRequest.getName());

        userRepository.persist(user);

        return Response.status(Response
                .Status.CREATED.getStatusCode())
                .entity(user)
                .build();
    }

    @POST
    @Path("/new")
    @Transactional
    public Response createUser2( User userRequest ) {

        userRepository.persist(userRequest);

        return Response.status(Response
                        .Status.CREATED.getStatusCode())
                .entity(userRequest)
                .build();
    }



    @PUT
    @Path("{id}")
    @Transactional
    public Response updateUser( @PathParam("id") Long id, CreateUserRequest userData) {

        User user = userRepository.findById(id);

       if(user != null) {
           user.setName(userData.getName());
           user.setAge(userData.getAge());
           return Response.noContent().build();
       }

        return Response.status(Response.Status.NOT_FOUND).build();

    }

    @DELETE
    @Path("{id}")
    @Transactional
    public Response deleteUser(@PathParam("id") Long id) {

        User user = userRepository.findById(id);

        if(user != null) {
            userRepository.delete(user);
            return Response.noContent().build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();

    }


}
