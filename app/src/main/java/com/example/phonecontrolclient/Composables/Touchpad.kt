import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import kotlin.math.abs

@Composable
fun Touchpad(
    modifier: Modifier = Modifier,
    width: Dp = 200.dp,
    height: Dp = 200.dp,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    moved: (dx: Float, dy: Float) -> Unit,
    onTap: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .pointerInput(Unit) {
                awaitEachGesture {
                    val down = awaitFirstDown()
                    var pastTouchSlop = false
                    var lastX = down.position.x
                    var lastY = down.position.y

                    drag(down.id) { change ->
                        val dx = change.position.x - lastX
                        val dy = change.position.y - lastY

                        if (!pastTouchSlop && (abs(dx) > 4 || abs(dy) > 4)) {
                            pastTouchSlop = true
                        }

                        if (pastTouchSlop) {
                            moved(dx, -dy)
                        }

                        lastX = change.position.x
                        lastY = change.position.y
                        change.consume()
                    }

                    if (!pastTouchSlop) {
                        onTap()
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
    }
}