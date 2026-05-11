package com.moment.momentbackend.bookmark.service;

import com.moment.momentbackend.bookmark.dto.BookmarkResponse;
import com.moment.momentbackend.bookmark.dto.BookmarkToggleResponse;
import com.moment.momentbackend.bookmark.entity.Bookmark;
import com.moment.momentbackend.bookmark.repository.BookmarkRepository;
import com.moment.momentbackend.global.exception.CustomException;
import com.moment.momentbackend.global.exception.ErrorCode;
import com.moment.momentbackend.program.entity.Program;
import com.moment.momentbackend.program.repository.ProgramRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final ProgramRepository programRepository;

    @Transactional
    public BookmarkToggleResponse toggle(Long userId, Long programId) {
        programRepository.findById(programId)
                .orElseThrow(() -> new CustomException(ErrorCode.PROGRAM_NOT_FOUND));

        Optional<Bookmark> existing = bookmarkRepository.findByUserIdAndProgramId(userId, programId);

        if (existing.isPresent()) {
            bookmarkRepository.delete(existing.get());
            return BookmarkToggleResponse.of(programId, false);
        } else {
            bookmarkRepository.save(Bookmark.of(userId, programId));
            return BookmarkToggleResponse.of(programId, true);
        }
    }

    @Transactional(readOnly = true)
    public List<BookmarkResponse> getBookmarkList(Long userId) {
        List<Bookmark> bookmarks = bookmarkRepository.findByUserIdOrderByCreatedAtDesc(userId);

        return bookmarks.stream()
                .map(bookmark -> programRepository.findById(bookmark.getProgramId())
                        .map(BookmarkResponse::from)
                        .orElse(null))
                .filter(response -> response != null)
                .collect(Collectors.toList());
    }
}
