package com.example.rafiq.data.repository

import com.example.rafiq.data.local.EquippedPlaceEntity
import com.example.rafiq.data.local.PlaceDao
import com.example.rafiq.domain.repository.PlaceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PlaceRepositoryImpl @Inject constructor(
    private val placeDao: PlaceDao
) : PlaceRepository {
    override fun getAllPlaces(): Flow<List<EquippedPlaceEntity>> {
        return placeDao.getAllPlaces()
    }

    override suspend fun getPlaceById(id: String): EquippedPlaceEntity? {
        return placeDao.getPlaceById(id)
    }

    override suspend fun insertPlace(place: EquippedPlaceEntity) {
        placeDao.insertPlace(place)
    }

    override suspend fun deletePlace(place: EquippedPlaceEntity) {
        placeDao.deletePlace(place)
    }
}
