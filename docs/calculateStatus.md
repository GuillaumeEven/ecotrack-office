
    /**
     * List<FloorWithStatusDto> calculateFloorsStatusForDate(Long organizationId, LocalDate date)
     *      J'instancie une liste de FloorWithStatusDto
     *      Je récupere tous les floors d'une organization
     *      Pour chaque floor:
     *            floorsWithStatus.add(new FloorWithStatusDto(floor, getRoomsStatus(floorId, date), date))
*                 Si c'est le premier floor:
        *            Je parcours tous les rooms du floor et je calcule leur status pour la date donnée:
        *            Si room.type == DESK_AREA:
        *                Si c'est la 1ère room de type DESK_AREA, available
        *                Si occupancy >= 80%:
        *                   Si room[i+1] existe et est de type DESK_AREA, room[i+1] est available
        *                   Sinon, floor.desksOccupied = true
        *            Sinon, si room.type == MEETING_ROOM:
        *                Si c'est la 1ère room de type MEETING_ROOM et n'est pas réservée, available
        *                Si la room est reservée:
        *                   Si room[i+1] existe et est de type MEETING_ROOM, room[i+1] est available
        *                   Sinon, floor.meetingRoomsOccupied = true
        *          Sinon:
        *                si floor[i-1].desksOccupied == true:
        *                   Je parcours tous les rooms du floor et je calcule leur status pour la date donnée:
        *                   Si room.type == DESK_AREA:
        *                      Si c'est la 1ère room de type DESK_AREA, available
        *                      Si occupancy >= 80%:
        *                         Si room[i+1] existe et est de type DESK_AREA, room[i+1] est available
        *                         Sinon, floor.desksOccupied = true
        *                si floor[i-1].meetingRoomsOccupied == true:
        *                   Je parcours tous les rooms du floor et je calcule leur status pour la date
        *                   Sinon, si room.type == MEETING_ROOM:
        *                      Si c'est la 1ère room de type MEETING_ROOM et n'est pas réservée, available

        *                      Si la room est reservée:
        *                         Si room[i+1] existe et est de type MEETING_ROOM, room[i+1] est available
        *                         Sinon, floor.meetingRoomsOccupied = true
     *
     * List<RoomWithStatusDto> calcalulateRoomsOccupancy(List<RoomEntity> rooms, LocalDate date)
     *      J'instancie une liste de RoomWithStatusDto
     *      Je cree une liste de room de type DESK_AREA
     *      List<RoomEntity> deskAreas = rooms.stream().filter(r -> r.getType() == RoomType.DESK_AREA).collect(Collectors.toList());
     *            Pour chaque room:
     *
                  List<DeskWithStatusDto> calculateDesksStatus(Long roomId, LocalDate date)
                  Calcul de l'occupancy rate de la room
                  
            Je cree une liste de room de type MEETING_ROOM
            List<RoomEntity> meetingRooms = rooms.stream().filter(r -> r.getType() == RoomType.MEETING_ROOM).collect(Collectors.toList());
            Pour chaque room:
                    si la room est reservée: status = 'Reserved'
                    sinon status = 'Unavailable'
            Je retourne la liste de RoomWithStatusDto
            return roomsWithStatus;
     */