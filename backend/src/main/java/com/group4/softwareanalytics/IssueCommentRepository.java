package com.group4.softwareanalytics;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "issueComment", path = "issueComment")
public interface IssueCommentRepository extends MongoRepository<IssueComment,String> {
    @Query("{'owner' : ?0 , 'repo' : ?1, 'issueNumber' : ?2}")
    Page<IssueComment> findByOwnerAndRepoAndIssueNumber(String owner, String repo, int number, Pageable pageable);

}
