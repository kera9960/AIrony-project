package com.example.aironyproject.domain.chat.dto.response;

import java.util.List;
import org.springframework.data.domain.Page;

public record GetAdminChatRoomsResponse(
    List<GetChatRoomListResponse> content,
    int page,
    int size,
    boolean hasNext,
    int totalPages,
    long totalElements
) {
  public static GetAdminChatRoomsResponse of(
      List<GetChatRoomListResponse> content,
      Page<?> page
  ) {
    return new GetAdminChatRoomsResponse(
        content,
        page.getNumber(),
        page.getSize(),
        page.hasNext(),
        page.getTotalPages(),
        page.getTotalElements()
    );
  }
}
