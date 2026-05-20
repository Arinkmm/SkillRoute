package com.skillroute.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "messages")
public class MessageProperties {
    private String internalServerError;
    private String validationError;
    private String validationSuccess;
    private Registration registration = new Registration();
    private PasswordReset passwordReset = new PasswordReset();
    private Verification verification = new Verification();
    private Account account = new Account();
    private Mail mail = new Mail();
    private Ui ui = new Ui();
    private Entity entity = new Entity();
    private Skill skill = new Skill();
    private Vacancy vacancy = new Vacancy();
    private Chat chat = new Chat();
    private Github github = new Github();

    @Getter
    @Setter
    public static class Registration {
        private String errorTitle;
        private String roleNotAllowed;
        private String emailExists;
        private String passwordMismatch;
        private String tooManyRequests;
        private String success;
    }

    @Getter
    @Setter
    public static class PasswordReset {
        private String errorTitle;
        private String tokenInvalid;
        private String passwordMismatch;
        private String tooManyRequests;
        private String emailSent;
        private String success;
    }

    @Getter
    @Setter
    public static class Verification {
        private String tokenInvalid;
        private String tooManyRequests;
        private String accountNotFound;
        private String alreadyVerified;
        private String resendSuccess;
        private String emailRequired;
        private String emailInvalid;
    }

    @Getter
    @Setter
    public static class Account {
        private String notFound;
        private String currentPasswordInvalid;
        private String passwordMismatch;
        private String samePassword;
        private String passwordUpdateError;
        private String passwordUpdated;
        private String profileUpdated;
        private String profileNamePairRequired;
    }

    @Getter
    @Setter
    public static class Mail {
        private String sendError;
    }

    @Getter
    @Setter
    public static class Ui {
        private String companyApproved;
        private String materialAdded;
        private String materialDeleted;
        private String vacancySaved;
        private String vacancyDeleted;
        private String vacancyClosed;
        private String roadmapSkillAdded;
        private String studentSkillAdded;
        private String vacancyApplied;
        private String githubSyncSuccess;
        private String githubSyncFailed;
        private String githubSyncStarted;
        private String githubSyncQueued;
        private String githubSyncWaiting;
        private String githubSyncError;
    }

    @Getter
    @Setter
    public static class Entity {
        private String accountNotFound;
        private String studentNotFound;
        private String companyNotFound;
        private String vacancyNotFound;
        private String skillNotFound;
        private String resourceNotFound;
        private String skillMissing;
        private String specializationNotFound;
        private String chatNotFound;
        private String noMessages;
    }

    @Getter
    @Setter
    public static class Skill {
        private String duplicate;
    }

    @Getter
    @Setter
    public static class Vacancy {
        private String duplicateTracking;
        private String editForbidden;
        private String deleteForbidden;
    }

    @Getter
    @Setter
    public static class Chat {
        private String accessForbidden;
        private String closed;
        private String studentStartForbidden;
    }

    @Getter
    @Setter
    public static class Github {
        private String urlRequired;
        private String loginExtractFailed;
        private String rateLimitExceeded;
        private String apiError;
        private String networkError;
    }
}
