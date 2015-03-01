package demo;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import demo.domain.Bookmark;
import demo.repository.AccountRepository;
import demo.repository.BookmarkRepository;
import demo.resources.BookmarkResource;

@RestController
@RequestMapping("/{userId}/bookmarks")
public class BookmarkRestController {

	private final BookmarkRepository bookmarkRepository;

	private final AccountRepository accountRepository;

	@RequestMapping(method = RequestMethod.POST)
	ResponseEntity<?> add(@PathVariable String userId, @RequestBody Bookmark input) {

        this.validateUser(userId);

        return accountRepository.findByUsername(userId)
                .map(account -> {
                            Bookmark bookmark = bookmarkRepository.save(new Bookmark(account, input.uri, input.description));

                            HttpHeaders httpHeaders = new HttpHeaders();

                            Link forOneBookmark = new BookmarkResource(bookmark).getLink("self");
                            httpHeaders.setLocation(URI.create(forOneBookmark.getHref()));

                            return new ResponseEntity<>(null, httpHeaders, HttpStatus.CREATED);
                        }
                ).get();
    }
	
	@RequestMapping(value = "/{bookmarkId}", method = RequestMethod.GET)
//	public Bookmark readBookmark(@PathVariable String userId, @PathVariable Long bookmarkId) {
//		this.validateUser(userId);
//		return this.bookmarkRepository.findOne(bookmarkId);
//	}
    public BookmarkResource readBookmark(@PathVariable String userId, @PathVariable Long bookmarkId) {
        this.validateUser(userId);
        return new BookmarkResource(this.bookmarkRepository.findOne(bookmarkId));
    }
    
	@RequestMapping(method = RequestMethod.GET)
//	Collection<Bookmark> readBookmarks(@PathVariable String userId) {
//		this.validateUser(userId);
//		return this.bookmarkRepository.findByAccountUsername(userId);
//	}
    Resources<BookmarkResource> readBookmarks(@PathVariable String userId) {

        this.validateUser(userId);

        List<BookmarkResource> bookmarkResourceList = bookmarkRepository.findByAccountUsername(userId)
                .stream()
                .map(BookmarkResource::new)
                .collect(Collectors.toList());
        return new Resources<BookmarkResource>(bookmarkResourceList);
    }
    
	@Autowired
	BookmarkRestController(BookmarkRepository bookmarkRepository,
			AccountRepository accountRepository) {
		this.bookmarkRepository = bookmarkRepository;
		this.accountRepository = accountRepository;
	}

	private void validateUser(String userId) {
		this.accountRepository.findByUsername(userId).orElseThrow(
				() -> new UserNotFoundException(userId));
	}
}
