package xyz.atom7.sharexspring.services.health

import org.springframework.boot.actuate.autoconfigure.system.DiskSpaceHealthIndicatorProperties
import org.springframework.stereotype.Component
import java.io.File

@Component
class DiskSpaceMonitorService(
    private val diskProps: DiskSpaceHealthIndicatorProperties
) {

    fun hasEnoughDiskSpace(): Boolean {
        return hasEnoughDiskSpace(0)
    }

    fun hasEnoughDiskSpace(toAdd: Long): Boolean {
        val path = diskProps.path ?: File(".")
        val thresholdBytes = diskProps.threshold.toBytes()
        return path.usableSpace + toAdd > thresholdBytes
    }

}