package aio.github.leovilela100.quarkussocial.domain.repository;

import aio.github.leovilela100.quarkussocial.domain.model.Follower;
import aio.github.leovilela100.quarkussocial.domain.model.User;
import aio.github.leovilela100.quarkussocial.rest.dto.FollowerResponse;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
public class FollowerRepository implements PanacheRepository<Follower> {

    public boolean follows(User follower, User user) {

        Map<String, Object> pararams =  Parameters.with("follower", follower).and("user", user).map();

       PanacheQuery<Follower> query =  find("follower = :follower and user = :user" , pararams);
        Optional<Follower> result =  query.firstResultOptional();

        return result.isPresent();
    }

    public List<Follower> findByUser (Long userId) {
        PanacheQuery<Follower> query =  find("user.id", userId);
         List<Follower> lista = query.list();
         return lista;
    }

    public void deleteByFollowerAndUser(Long followerId, Long userId) {

        var params = Parameters.with("userId", userId).and("followerId", followerId).map();

        delete("follower.id =:followerId and user.id =:userId", params);
    }
}
