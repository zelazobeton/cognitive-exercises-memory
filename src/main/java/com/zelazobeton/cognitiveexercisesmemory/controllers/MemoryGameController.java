package com.zelazobeton.cognitiveexercisesmemory.controllers;

import static com.zelazobeton.cognitiveexercisesmemory.constants.FileConstants.FORWARD_SLASH;
import static com.zelazobeton.cognitiveexercisesmemory.constants.FileConstants.MEMORY_IMG_FOLDER;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

import java.io.IOException;
import java.security.Principal;
import javax.annotation.security.RolesAllowed;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zelazobeton.cognitiveexercisesmemory.ExceptionHandling;
import com.zelazobeton.cognitiveexercisesmemory.HttpResponse;
import com.zelazobeton.cognitiveexercisesmemory.constants.MessageConstants;
import com.zelazobeton.cognitiveexercisesmemory.domain.User;
import com.zelazobeton.cognitiveexercisesmemory.model.MemoryBoardDto;
import com.zelazobeton.cognitiveexercisesmemory.services.ExceptionMessageService;
import com.zelazobeton.cognitiveexercisesmemory.services.MemoryGameService;
import com.zelazobeton.cognitiveexercisesmemory.services.ResourceService;
import com.zelazobeton.cognitiveexercisesmemory.services.UserService;

@RestController
@RequestMapping(path = "/v1")
public class MemoryGameController extends ExceptionHandling {
    private final MemoryGameService memoryGameService;
    private final ResourceService resourceService;
    private final UserService userService;

    public MemoryGameController(ExceptionMessageService exceptionMessageService, MemoryGameService memoryGameService,
            ResourceService resourceService, UserService userService) {
        super(exceptionMessageService);
        this.memoryGameService = memoryGameService;
        this.resourceService = resourceService;
        this.userService = userService;
    }

    @GetMapping(path = "/img/{fileName}", produces = IMAGE_JPEG_VALUE)
    public byte[] getMemoryTileImage(@PathVariable("fileName") String fileName) throws IOException {
        return this.resourceService.getResource(MEMORY_IMG_FOLDER + FORWARD_SLASH + fileName);
    }

    @GetMapping(path = "/game", produces = { "application/json" }, params = "level")
    @RolesAllowed("ROLE_ce-user")
    public ResponseEntity<MemoryBoardDto> getNewMemoryBoard( @RequestParam("level") String difficultyLvl) {
        MemoryBoardDto board = this.memoryGameService.getNewMemoryBoardDto(difficultyLvl);
        return new ResponseEntity<>(board, HttpStatus.OK);
    }

    @GetMapping(path = "/game", produces = { "application/json" })
    @RolesAllowed("ROLE_ce-user")
    public ResponseEntity<MemoryBoardDto> getSavedMemoryBoard(Principal principal) {
        MemoryBoardDto board = this.memoryGameService.getSavedMemoryBoardDto(principal.getName());
        return new ResponseEntity<>(board, HttpStatus.OK);
    }

    @PostMapping(path = "/game")
    @RolesAllowed("ROLE_ce-user")
    public ResponseEntity<HttpResponse> saveGame(Principal principal,
            @RequestBody MemoryBoardDto memoryBoardDto) {
        User user = this.userService.findUserOrCreateNewOne(principal);
        this.memoryGameService.saveGame(user, memoryBoardDto);
        return new ResponseEntity<>(new HttpResponse(OK, this.exceptionMessageService.getMessage(MessageConstants.MEMORY_GAME_CONTROLLER_GAME_SAVED)), OK);
    }

    @PostMapping(path = "/score")
    @RolesAllowed("ROLE_ce-user")
    public ResponseEntity<Integer> saveScore(Principal principal,
            @RequestBody MemoryBoardDto memoryBoardDto) {
        return new ResponseEntity<>(this.memoryGameService.saveScore(principal, memoryBoardDto),
                HttpStatus.OK);
    }
}
