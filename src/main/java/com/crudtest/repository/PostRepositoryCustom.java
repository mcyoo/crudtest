package com.crudtest.repository;

import com.crudtest.domain.Post;
import com.crudtest.request.PostSearch;

import java.util.List;

public interface PostRepositoryCustom {

    List<Post> getList(PostSearch postSearch,String userKey);
}
