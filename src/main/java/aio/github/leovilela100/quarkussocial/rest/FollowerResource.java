package aio.github.leovilela100.quarkussocial.rest;

import aio.github.leovilela100.quarkussocial.domain.model.Follower;
import aio.github.leovilela100.quarkussocial.domain.model.User;
import aio.github.leovilela100.quarkussocial.domain.repository.FollowerRepository;
import aio.github.leovilela100.quarkussocial.domain.repository.UserRepository;
import aio.github.leovilela100.quarkussocial.rest.dto.FollowerRequest;
import aio.github.leovilela100.quarkussocial.rest.dto.FollowerResponse;
import aio.github.leovilela100.quarkussocial.rest.dto.FollowersPerUserResponse;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.stream.Collectors;

@Path("/users/{userId}/followers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FollowerResource {

    private final FollowerRepository followerRepository;
    private final UserRepository userRepository;

    @Inject
    public FollowerResource(FollowerRepository followerRepository, UserRepository userRepository) {
        this.followerRepository = followerRepository;
        this.userRepository = userRepository;
    }


    @GET
    public Response listFollowers( @PathParam("userId") Long userId) {

        User user = userRepository.findById(userId);

        if(user == null) {
            return  Response.status(Response.Status.NOT_FOUND).build();
        }

        List<Follower> list = followerRepository.findByUser(userId);

        FollowersPerUserResponse responseObject = new FollowersPerUserResponse();
        responseObject.setFollowersCount(list.size());
        List<FollowerResponse> followerList = list.stream().map(FollowerResponse::new).collect(Collectors.toList());
        responseObject.setContent(followerList);
        return Response.ok(responseObject).build();
    }

    @PUT
    @Transactional
    public Response followUser(@PathParam("userId") Long userId, FollowerRequest request ) {

        if(userId.equals(request.getFollowerId())) {
            System.out.println("Caiu aqui");
            return  Response.status(Response.Status.CONFLICT).entity("You can't follow yourself ").build();
        }

        User user = userRepository.findById(userId);

        if(user == null) {
            return  Response.status(Response.Status.NOT_FOUND).build();
        }

         User follower =  userRepository.findById(request.getFollowerId());

        boolean followers = followerRepository.follows(follower, user);

            if(!followers) {
                Follower entity = new Follower();

                entity.setUser(user);
                entity.setFollower(follower);

                followerRepository.persist(entity);

            }

        return Response.status(Response.Status.NO_CONTENT).build();

    }

    @DELETE
    @Transactional
    public Response unFollowUser( @PathParam ("userId") Long userId, @QueryParam("followerId") Long followerId) {

        User user = userRepository.findById(userId);

        if(user == null) {
            return  Response.status(Response.Status.NOT_FOUND).build();
        }

        followerRepository.deleteByFollowerAndUser(followerId, userId);

        return Response.status(Response.Status.NO_CONTENT).build();

    }
}
