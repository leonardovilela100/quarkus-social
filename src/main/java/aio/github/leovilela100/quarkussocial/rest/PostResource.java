package aio.github.leovilela100.quarkussocial.rest;

import aio.github.leovilela100.quarkussocial.domain.model.Follower;
import aio.github.leovilela100.quarkussocial.domain.model.Post;
import aio.github.leovilela100.quarkussocial.domain.model.User;
import aio.github.leovilela100.quarkussocial.domain.repository.FollowerRepository;
import aio.github.leovilela100.quarkussocial.domain.repository.PostRepository;
import aio.github.leovilela100.quarkussocial.domain.repository.UserRepository;
import aio.github.leovilela100.quarkussocial.rest.dto.CreatePostRequest;
import aio.github.leovilela100.quarkussocial.rest.dto.PostResponse;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.stream.Collectors;

@Path("/users/{userId}/posts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PostResource {


    private final UserRepository userRepository;

    private final PostRepository postRepository;

    private final FollowerRepository followerRepository;


    public PostResource(UserRepository userRepository, PostRepository postRepository, FollowerRepository followerRepository) {

        this.userRepository = userRepository;


        this.postRepository = postRepository;

        this.followerRepository = followerRepository;
    }

    @GET
    public Response listPost( @PathParam("userId") Long userId, @HeaderParam("followerId") Long followerId ) {

        User user = userRepository.findById(userId);

        if(user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        if(followerId == null) {
            return  Response.status(Response.Status.BAD_REQUEST).entity("You forgot the header followerId").build();
        }

        User follower = userRepository.findById(followerId);

        if(follower == null) {
            return  Response.status(Response.Status.BAD_REQUEST).entity("Inexistent followerId").build();

        }

        boolean follows = followerRepository.follows(follower, user);

        if(!follows) {
           return Response.status(Response.Status.FORBIDDEN).entity("You can't see these posts").build();
        }

        PanacheQuery<Post> query = postRepository.find("user", Sort.by("dateTime", Sort.Direction.Descending) , user);

        var list = query.list();

        var postResponseList = list.stream()
                //.map(post -> PostResponse.fromEntity(post))
                .map( PostResponse::fromEntity)
                .collect(Collectors.toList());

        return Response.ok(postResponseList).build();

    }

    @POST
    @Transactional
    public Response savePost(@PathParam("userId") Long userId, CreatePostRequest request) {

        User user = userRepository.findById(userId);

        if(user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Post post = new Post();

        post.setText(request.getText());
        post.setUser(user);

        postRepository.persist(post);

        return Response.status(Response.Status.CREATED.getStatusCode()).entity(post).build();
    }

}
