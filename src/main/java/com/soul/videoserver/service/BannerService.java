package com.soul.videoserver.service;


import com.soul.videoserver.entity.Banner;

import java.util.List;

public interface BannerService {
    List<Banner> getBannerList();
    void createBanner(Banner banner);
    void deleteBanner(String id);

    void updateBanner(Banner banner);
}
