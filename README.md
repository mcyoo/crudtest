# crudtest
CRUD TEST

https://crudtest.oopy.io/ 

### 안녕하세요. CRUD TEST 입니다.

간단한 TEST 용 DB를 제공합니다. 블로그, 게시물 등에 TEST 로 활용할 수 있습니다.

> **C (Create) 게시물 생성**
> 

<aside>
💡 최대 100개의 게시물을 생성할 수 있습니다.

</aside>

`POST`  `https://crudtest.link/posts?key={인증키}`

- request
    
    ```json
    Content-Type: application/json
    ```
    
    ```json
    {
    	"title": "제목 입니다."
    	"content": "내용 입니다."
    }
    ```
    
- response
    
    ```
    HTTP 200
    ```
    
    ```json
    {
    	"postId": 1
    }
    ```
    

> **R (Read) 게시물 읽기 | page, size 값 변경 가능**
> 

`GET`  `https://crudtest.link/posts?page={1}&size={10}&key={인증키}`

`GET`  `https://crudtest.link/posts/{게시물 ID}?key={인증키}`

- response
    
    ```
    HTTP 200
    ```
    
    ```json
    [
    	{
    		"id": 1,
    		"title": "제목 입니다.",
    		"content": "내용 입니다."
    		"good_count": 5,
    		"bad_count": 10,
    		"createDate": "2022-07-26 14:10",
    		"modifiedDate": "2022-07-26 14:32"
    	},
    	...
    ]
    ```
    

> **U (Update) 게시물 수정**
> 

`PATCH`  `https://crudtest.link/posts/{게시물 ID}?key={인증키}`

- request
    
    ```json
    Content-Type: application/json
    ```
    
    ```json
    {
    	"title": "제목 입니다. 2"
    	"content": "내용 입니다. 2"
    }
    ```
    
- response
    
    ```
    HTTP 200
    ```
    
    ```json
    없음
    ```
    

> **D (Delete) 게시물 삭제**
> 

`DELETE`  `https://crudtest.link/posts/{게시물 ID}?key={인증키}`

- response
    
    ```
    HTTP 200
    ```
    
    ```json
    없음
    ```
    

> **게시물 좋아요, 싫어요 숫자 +1**
> 

**`POST`**  `https://crudtest.link/posts/{게시물 ID}/good_count?key={인증키}`

**`POST`**  `https://crudtest.link/posts/{게시물 ID}/bad_count?key={인증키}`

- response
    
    ```
    HTTP 200
    ```
    
    ```json
    {
    	"postId": 1,
    	"good_count": 2
    }
    ```
