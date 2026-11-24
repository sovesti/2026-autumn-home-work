package company.vk.edu.distrib.compute.sovesti.moderation;

import java.io.IOException;

import company.vk.edu.distrib.compute.moderation.ModerationDeadLetterTest;
import company.vk.edu.distrib.compute.moderation.ModerationService;
import company.vk.edu.distrib.compute.moderation.ModerationServiceFactory;
import company.vk.edu.distrib.compute.moderation.ModerationServiceFactoryTest;

@ModerationServiceFactoryTest
@ModerationDeadLetterTest
public final class VfedorovModerationServiceFactory implements ModerationServiceFactory {

    @Override
    public ModerationService create(String servers) throws IOException {
        return new VfedorovModerationService(servers);
    }

}
