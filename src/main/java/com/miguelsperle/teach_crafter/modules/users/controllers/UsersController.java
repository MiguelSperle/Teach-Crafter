package com.miguelsperle.teach_crafter.modules.users.controllers;

import com.miguelsperle.teach_crafter.dtos.general.CustomAuthenticationEntryResponseDTO;
import com.miguelsperle.teach_crafter.dtos.general.MessageResponseDTO;
import com.miguelsperle.teach_crafter.modules.users.dtos.users.*;
import com.miguelsperle.teach_crafter.modules.users.entities.users.UsersEntity;
import com.miguelsperle.teach_crafter.modules.users.services.UsersService;
import com.miguelsperle.teach_crafter.modules.users.services.RequestFieldValidationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

@RestController
@RequestMapping("/users")
public class UsersController {
    private final UsersService usersService;
    private final RequestFieldValidationService requestFieldValidationService;

    public UsersController(final UsersService usersService, final RequestFieldValidationService requestFieldValidationService) {
        this.usersService = usersService;
        this.requestFieldValidationService = requestFieldValidationService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "This route is responsible for handling user registration")
    @ApiResponses({
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponseDTO.class),
                    examples = {
                            @ExampleObject(value = "{\"message\": \"Account created successfully\", \"status\": 201}")
                    })),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponseDTO.class),
                    examples = {
                            @ExampleObject(name = "Missing Role", description = "Error returned because the role is missing in the request", value = "{\"message\": \"Role is required to create an account\", \"status\": 400}"),
                            @ExampleObject(name = "Missing Name", description = "Error returned because the name is missing in the request", value = "{\"message\": \"Name is required to create an account\", \"status\": 400}"),
                            @ExampleObject(name = "Missing Email", description = "Error returned because the email is missing in the request", value = "{\"message\": \"Email is required to create an account\", \"status\": 400}"),
                            @ExampleObject(name = "Invalid Username", description = "Error returned because the username does not contain a valid value", value = "{\"message\": \"The field [username] is required and must not contain space\", \"status\": 400}"),
                            @ExampleObject(name = "Invalid Email", description = "Error returned because the email does not contain a valid value", value = "{\"message\": \"The field [email] must contain a valid email\", \"status\": 400}"),
                            @ExampleObject(name = "Invalid Password", description = "Error returned because the password does not contain between 5 and 100 character", value = "{\"message\": \"Password must has between 5 and 100 character\", \"status\": 400}")
                    })),
            @ApiResponse(responseCode = "409", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponseDTO.class),
                    examples = {
                            @ExampleObject(name = "User Already Exists", description = "Error returned because an attempt was made to register a user with a username/email address that is already in use", value = "{\"message\": \"User already exists\", \"status\": 409}")
                    })),
    })
    public ResponseEntity<Object> registerUser(@RequestBody @Valid CreateUserDTO createUserDTO, BindingResult bindingResult) {
        this.requestFieldValidationService.validationErrors(bindingResult);

        this.usersService.createUser(createUserDTO);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MessageResponseDTO("Account created successfully", HttpStatus.CREATED.value()));
    }


    @PutMapping("/update-name")
    @Operation(summary = "Update user name", description = "This route is responsible for allowing a user to update their name")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponseDTO.class),
                    examples = {
                            @ExampleObject(value = "{\"message\": \"Name updated successfully\", \"status\": 200}")
                    })),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponseDTO.class),
                    examples = {
                            @ExampleObject(name = "Missing New Name", description = "Error returned because the new name is missing in the request", value = "{\"message\": \"A new name is required to update your current name\", \"status\": 400}"),
                    })),
            @ApiResponse(responseCode = "403", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomAuthenticationEntryResponseDTO.class),
                    examples = {
                            @ExampleObject(name = "Missing Authorization Token", description = "Error returned because authorization token is missing in the request header", value = "{\"message\": \"Authorization token missing in request header\", \"status\": 403}"),
                            @ExampleObject(name = "Invalid Authorization Token", description = "Error returned because authorization token is invalid", value = "{\"message\": \"Invalid authorization token\", \"status\": 403}"),
                            @ExampleObject(name = "Failure During Authentication", description = "Error returned because a failure occurred during authentication", value = "{\"message\": \"Authentication failed\", \"status\": 403}")
                    })),
    })
    @SecurityRequirement(name = "jwt_auth")
    public ResponseEntity<Object> updateUserName(@RequestBody @Valid UpdateUserNameDTO updateNameUserDTO, BindingResult bindingResult) {
        this.requestFieldValidationService.validationErrors(bindingResult);

        this.usersService.updateUserName(updateNameUserDTO);

        return ResponseEntity.ok()
                .body(new MessageResponseDTO("Name updated successfully", HttpStatus.OK.value()));
    }

    @PutMapping("/update-username")
    @Operation(summary = "Update user username", description = "This route is responsible for allowing a user to update their username")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponseDTO.class),
                    examples = {
                            @ExampleObject(value = "{\"message\": \"Username updated successfully\", \"status\": 200}")
                    })),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponseDTO.class),
                    examples = {
                            @ExampleObject(name = "Missing Current Password", description = "Error returned because the current password is missing in the request", value = "{\"message\": \"Your current password is required to update your username\", \"status\": 400}"),
                            @ExampleObject(name = "Invalid New Username", description = "Error returned because the new username does not contain a valid value", value = "{\"message\": \"The field [newUsername] is required and must not contain space\", \"status\": 400}")
                    })),
            @ApiResponse(responseCode = "401", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponseDTO.class),
                    examples = {
                            @ExampleObject(name = "Incorrect Current Password", description = "Error returned because the user provided the incorrect current  password to update their username", value = "{\"message\": \"Incorrect current password\", \"status\": 401}"),
                    })),
            @ApiResponse(responseCode = "403", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomAuthenticationEntryResponseDTO.class),
                    examples = {
                            @ExampleObject(name = "Missing Authorization Token", description = "Error returned because authorization token is missing in the request header", value = "{\"message\": \"Authorization token missing in request header\", \"status\": 403}"),
                            @ExampleObject(name = "Invalid Authorization Token", description = "Error returned because authorization token is invalid", value = "{\"message\": \"Invalid authorization token\", \"status\": 403}"),
                            @ExampleObject(name = "Failure During Authentication", description = "Error returned because a failure occurred during authentication", value = "{\"message\": \"Authentication failed\", \"status\": 403}")
                    })),
            @ApiResponse(responseCode = "409", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponseDTO.class),
                    examples = {
                            @ExampleObject(name = "Username Already Used", description = "Error returned because an attempt was made to update the username to one that is already in use", value = "{\"message\": \"This username is already used\", \"status\": 409}")
                    }))
    })
    @SecurityRequirement(name = "jwt_auth")
    public ResponseEntity<Object> updateUserUsername(@RequestBody @Valid UpdateUserUsernameDTO updateUserUsernameDTO, BindingResult bindingResult) {
        this.requestFieldValidationService.validationErrors(bindingResult);

        this.usersService.updateUserUsername(updateUserUsernameDTO);

        return ResponseEntity.ok()
                .body(new MessageResponseDTO("Username updated successfully", HttpStatus.OK.value()));
    }

    @PutMapping("/update-email")
    @Operation(summary = "Update user email", description = "This route is responsible for allowing a user to update their email")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponseDTO.class),
                    examples = {
                            @ExampleObject(value = "{\"message\": \"Email updated successfully\", \"status\": 200}")
                    })),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponseDTO.class),
                    examples = {
                            @ExampleObject(name = "Missing New Email", description = "Error returned because the new email is missing in the request", value = "{\"message\": \"A new email is required to update your current email\", \"status\": 400}"),
                            @ExampleObject(name = "Missing Current Password", description = "Error returned because the current password is missing in the request", value = "{\"message\": \"Your current password is required to update your email\", \"status\": 400}"),
                            @ExampleObject(name = "Invalid New Email", description = "Error returned because the new email does not contain a valid value", value = "{\"message\": \"The field [newEmail] must contain a valid email\", \"status\": 400}")
                    })),
            @ApiResponse(responseCode = "401", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponseDTO.class),
                    examples = {
                            @ExampleObject(name = "Incorrect Current Password", description = "Error returned because the user provided the incorrect current password to update their email", value = "{\"message\": \"Incorrect current password\", \"status\": 401}"),
                    })),
            @ApiResponse(responseCode = "403", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomAuthenticationEntryResponseDTO.class),
                    examples = {
                            @ExampleObject(name = "Missing Authorization Token", description = "Error returned because authorization token is missing in the request header", value = "{\"message\": \"Authorization token missing in request header\", \"status\": 403}"),
                            @ExampleObject(name = "Invalid Authorization Token", description = "Error returned because authorization token is invalid", value = "{\"message\": \"Invalid authorization token\", \"status\": 403}"),
                            @ExampleObject(name = "Failure During Authentication", description = "Error returned because a failure occurred during authentication", value = "{\"message\": \"Authentication failed\", \"status\": 403}")
                    })),
            @ApiResponse(responseCode = "409", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponseDTO.class),
                    examples = {
                            @ExampleObject(name = "Email Already Used", description = "Error returned because an attempt was made to update the email to one that is already in use", value = "{\"message\": \"This email is already used\", \"status\": 409}")
                    }))
    })
    @SecurityRequirement(name = "jwt_auth")
    public ResponseEntity<Object> updateUserEmail(@RequestBody @Valid UpdateUserEmailDTO updateUserEmailDTO, BindingResult bindingResult) {
        this.requestFieldValidationService.validationErrors(bindingResult);

        this.usersService.updateUserEmail(updateUserEmailDTO);

        return ResponseEntity.ok()
                .body(new MessageResponseDTO("Email updated successfully", HttpStatus.OK.value()));
    }

    @PutMapping("/update-password")
    @Operation(summary = "Update user password", description = "This route is responsible for allowing a user to update their password")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponseDTO.class),
                    examples = {
                            @ExampleObject(value = "{\"message\": \"Password updated successfully\", \"status\": 200}")
                    })),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponseDTO.class),
                    examples = {
                            @ExampleObject(name = "Missing Current Password", description = "Error returned because the current password is missing in the request", value = "{\"message\": \"Your current password is required to update your password\", \"status\": 400}"),
                            @ExampleObject(name = "Invalid New Password", description = "Error returned because the new password does not contain between 5 and 100 character", value = "{\"message\": \"Password must has between 5 and 100 character\", \"status\": 400}")
                    })),
            @ApiResponse(responseCode = "401", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponseDTO.class),
                    examples = {
                            @ExampleObject(name = "Incorrect Current Password", description = "Error returned because the user provided the incorrect current password to update their password", value = "{\"message\": \"Incorrect current password\", \"status\": 401}"),
                    })),
            @ApiResponse(responseCode = "403", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomAuthenticationEntryResponseDTO.class),
                    examples = {
                            @ExampleObject(name = "Missing Authorization Token", description = "Error returned because authorization token is missing in the request header", value = "{\"message\": \"Authorization token missing in request header\", \"status\": 403}"),
                            @ExampleObject(name = "Invalid Authorization Token", description = "Error returned because authorization token is invalid", value = "{\"message\": \"Invalid authorization token\", \"status\": 403}"),
                            @ExampleObject(name = "Failure During Authentication", description = "Error returned because a failure occurred during authentication", value = "{\"message\": \"Authentication failed\", \"status\": 403}")
                    })),
    })
    @SecurityRequirement(name = "jwt_auth")
    public ResponseEntity<Object> updateLoggedUserPassword(@RequestBody @Valid UpdateLoggedUserPasswordDTO updateLoggedUserPasswordDTO, BindingResult bindingResult) {
        this.requestFieldValidationService.validationErrors(bindingResult);

        this.usersService.updateLoggedUserPassword(updateLoggedUserPasswordDTO);

        return ResponseEntity.ok()
                .body(new MessageResponseDTO("Password updated successfully", HttpStatus.OK.value()));
    }

    @PutMapping(value = "/update-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Update user image", description = "This route is responsible for allowing a user to update their image")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponseDTO.class),
                    examples = {
                            @ExampleObject(value = "{\"message\": \"Image updated successfully\", \"status\": 200}")
                    })),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponseDTO.class),
                    examples = {
                            @ExampleObject(name = "Missing Image File", description = "Error returned because image file is missing in the request", value = "{\"message\": \"Image file is required\", \"status\": 400}")
                    })),
            @ApiResponse(responseCode = "403", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomAuthenticationEntryResponseDTO.class),
                    examples = {
                            @ExampleObject(name = "Missing Authorization Token", description = "Error returned because authorization token is missing in the request header", value = "{\"message\": \"Authorization token missing in request header\", \"status\": 403}"),
                            @ExampleObject(name = "Invalid Authorization Token", description = "Error returned because authorization token is invalid", value = "{\"message\": \"Invalid authorization token\", \"status\": 403}"),
                            @ExampleObject(name = "Failure During Authentication", description = "Error returned because a failure occurred during authentication", value = "{\"message\": \"Authentication failed\", \"status\": 403}")
                    })),
    })
    @SecurityRequirement(name = "jwt_auth")
    public ResponseEntity<Object> updateUserImage(@RequestPart(value = "imageFile", required = false) MultipartFile imageFile) {
        if (imageFile == null || imageFile.isEmpty())
            return ResponseEntity.badRequest().body(new MessageResponseDTO("Image file is required", HttpStatus.BAD_REQUEST.value()));

        this.usersService.updateUserImage(imageFile);

        return ResponseEntity.ok()
                .body(new MessageResponseDTO("Image updated successfully", HttpStatus.OK.value()));
    }

    @GetMapping("/profile")
    @Operation(summary = "Fetch authenticated user profile", description = "This route is responsible for fetching the authenticated user profile")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "403", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomAuthenticationEntryResponseDTO.class),
                    examples = {
                            @ExampleObject(name = "Missing Authorization Token", description = "Error returned because authorization token is missing in the request header", value = "{\"message\": \"Authorization token missing in request header\", \"status\": 403}"),
                            @ExampleObject(name = "Invalid Authorization Token", description = "Error returned because authorization token is invalid", value = "{\"message\": \"Invalid authorization token\", \"status\": 403}"),
                            @ExampleObject(name = "Failure During Authentication", description = "Error returned because a failure occurred during authentication", value = "{\"message\": \"Authentication failed\", \"status\": 403}")
                    })),
    })
    @SecurityRequirement(name = "jwt_auth")
    public UserResponseDTO getAuthenticatedUserProfile() {
        UsersEntity user = this.usersService.getAuthenticatedUser();

        return new UserResponseDTO(user.getUsername(), user.getRole(), user.getName(), user.getEmail(), user.getAvatarUrl(), user.getCreatedAt());
    }
}
