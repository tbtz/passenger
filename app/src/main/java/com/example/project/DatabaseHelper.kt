package com.example.project

import android.content.Context
import androidx.room.*

@Entity
data class Credentials(
    @PrimaryKey(autoGenerate = true) val uid: Long? = null,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "password") var password: String
)


fun Credentials.encrypt(secret: String): Credentials {
    return Credentials(
        uid= this.uid,
        title = this.title.encrypt(secret),
        password = this.password.encrypt(secret)
    )
}

fun Credentials.decrypt(secret: String): Credentials {
    return Credentials(
        uid= this.uid,
        title = this.title.decrypt(secret),
        password = this.password.decrypt(secret)
    )
}


@Dao
interface CredentialsRepository {
    @Query("SELECT * FROM credentials")
    fun findAll(): List<Credentials>

    @Query("SELECT * FROM credentials WHERE uid = :id")
    fun findById(id: Long): Credentials

    @Insert
    fun insertAll(vararg credentials: Credentials)

    @Update
    fun update(vararg credentials: Credentials)

    @Delete
    fun delete(credentials: Credentials)
}

@Database(entities = [Credentials::class], version = 1)
abstract class PassengerDatabase : RoomDatabase() {
    abstract fun credentials(): CredentialsRepository
}

fun initDatabase(context: Context): PassengerDatabase {
    return Room.databaseBuilder(
        context,
        PassengerDatabase::class.java, "passenger.db"
    )
        .allowMainThreadQueries()
        .build()
}