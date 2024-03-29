import {useEffect, useState} from "react";
import {
    IconButton,
    useTheme,
    useMediaQuery,
    Typography,
    Box, Link,
} from "@mui/material";
import InfoIcon from "@mui/icons-material/Info";

function Legend({title, text, changePageToInfo, children}) {
    const [minimized, setMinimized] = useState(false);
    const theme = useTheme();
    const isMobile = useMediaQuery(theme.breakpoints.down("sm"));

    useEffect(() => {
        if (!isMobile) {
            setMinimized(false);
        }
    }, [isMobile]);

    const toggleMinimized = () => {
        setMinimized((prevMinimized) => !prevMinimized);
    };

    const handleMoreInfoClick = () => {
        changePageToInfo();
    }

    return (
        <Box sx={{
            position: "absolute",
            right: "2vw",
            marginTop: "2vw",
            padding: "2vw",
            backgroundColor: theme.palette.background.paper,
            ...(isMobile && {
                left: "4vw",
                right: "4vw",
                marginTop: "4vw",
            }),
        }}>
            <Box sx={{
                display: "flex",
                justifyContent: "space-between",
                alignItems: "center"}}>
                <Typography variant="h6" fontWeight="bold">
                    {title}
                </Typography>
                {isMobile && (
                    <IconButton
                        className="minimize-button"
                        onClick={toggleMinimized}
                    >
                        <InfoIcon/>
                    </IconButton>
                )}
            </Box>

            {!minimized && (
                <>
                    <Box maxWidth={380} textAlign={"justify"}>
                        <Typography variant="body1">
                            {text}
                            <Link
                                component="button"
                                variant="body2"
                                onClick={handleMoreInfoClick}
                                sx={{ml: 1, mb: 0.5}}
                                underline="none"
                            >
                                [More]
                            </Link>
                        </Typography>
                    </Box>
                    {children}
                </>
            )}
        </Box>
    );
}

export default Legend;
