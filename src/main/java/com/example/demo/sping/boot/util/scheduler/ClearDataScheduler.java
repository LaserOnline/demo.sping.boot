package com.example.demo.sping.boot.util.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.demo.sping.boot.service.removefile.RemoveFileService;
import com.example.demo.sping.boot.util.repository.ContentImagesRepository;
import com.example.demo.sping.boot.util.repository.ContentRepository;
import com.example.demo.sping.boot.util.repository.UserRepository;
import com.example.demo.sping.boot.util.repository.UsersInfoRepository;

@Component
public class ClearDataScheduler {
private final RemoveFileService removeFileService;
    private final UserRepository userRepository;
    private final UsersInfoRepository usersInfoRepository;
    private final ContentRepository contentRepository;
    private final ContentImagesRepository contentImagesRepository;
    public  ClearDataScheduler (
        RemoveFileService removeFileService, 
        UserRepository userRepository, 
        UsersInfoRepository usersInfoRepository,
        ContentRepository contentRepository,
        ContentImagesRepository contentImagesRepository
    ) {
        this.userRepository = userRepository;
        this.usersInfoRepository = usersInfoRepository;
        this.removeFileService = removeFileService;
        this.contentRepository = contentRepository;
        this.contentImagesRepository = contentImagesRepository;
    }
    @Scheduled(cron = "0 0 */3 * * *")
                //     0 0 */3 * * *
                // │ │ │   │ │ └─ วันในสัปดาห์ (ทุกวัน)
                // │ │ │   │ └── เดือน (ทุกเดือน)
                // │ │ │   └──── วันในเดือน (ทุกวัน)
                // │ │ └──────── ชั่วโมง (ทุก 3 ชั่วโมง)
                // │ └────────── นาที (0)
                // └──────────── วินาที (0)
    public void clearAll() {
        userRepository.deleteAll();
        usersInfoRepository.deleteAll();
        contentRepository.deleteAll();
        contentImagesRepository.deleteAll();
        removeFileService.clearAllFilesInUploadPath();
        System.out.println("clear, all! ✅");
    }
}
