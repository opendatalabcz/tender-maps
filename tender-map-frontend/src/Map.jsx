import HeatMap from './HeatMap.jsx';
import IconMap from './IconMap.jsx';
import HexagonMap from './HexagonMap.jsx';
import {useCallback, useState} from 'react'
import FilterSideBar from "./FilterSideBar.jsx";
import IconButton from "@mui/material/IconButton";
import FilterAltIcon from '@mui/icons-material/FilterAlt';
import {useMediaQuery, useTheme} from "@mui/material";
import {toast, ToastContainer} from "react-toastify";
import axios from "axios";
import 'react-toastify/dist/ReactToastify.css';

const MAP_STYLES = {
    withLabels: {
        light: "https://basemaps.cartocdn.com/gl/positron-gl-style/style.json",
        dark: "https://basemaps.cartocdn.com/gl/dark-matter-gl-style/style.json"
    },
    noLabels: {
        dark: "https://basemaps.cartocdn.com/gl/dark-matter-nolabels-gl-style/style.json"
    }
};

const INITIAL_VIEW_STATE = {
    longitude: 15.301806,
    latitude: 49.868280,
    zoom: 6.6,
    maxZoom: 17,
    pitch: 0,
    bearing: 0
};

function Map({activeMap, apiBaseUrl, isDarkMode, changePageToInfo}) {
    const [filterLocations, setFilterLocations] = useState([]);
    const [filterAuthorities, setFilterAuthorities] = useState([]);
    const [showFilterMenu, setShowFilterMenu] = useState(false);
    const [viewState, setViewState] = useState(INITIAL_VIEW_STATE);
    const theme = useTheme();
    const isMobile = useMediaQuery(theme.breakpoints.down("sm"));
    const fetchData = useCallback(async (path, setFetchedData) => {
        try {
            const url = apiBaseUrl + path;
            const response = await axios.get(url);
            setFetchedData(response.data);
        } catch (error) {
            console.error("Error fetching data:", error);
            toast.error("Failed to fetch data.", {
                toastId: "toastId", //
                position: "top-center",
                autoClose: 1500,
                hideProgressBar: false,
                closeOnClick: true,
                pauseOnHover: false,
                draggable: true,
                progress: undefined,
                theme: "light",
            });
        }
    }, [apiBaseUrl]);

    const addFiltersToPath = useCallback((path, additionalParams) => {
        const params = new URLSearchParams({
            ...additionalParams,
        });
        if (filterLocations.length !== 0) {
            const placesOfPerformanceParam = filterLocations.join(',');
            params.append("placesOfPerformance", placesOfPerformanceParam);
        }
        if (filterAuthorities.size !== 0) {
            const filterAuthoritiesIDsParam = [...filterAuthorities].map((authority) => authority.id).join(',');
            params.append("contractingAuthorityIds", filterAuthoritiesIDsParam);
        }
        return `${path}?${params.toString()}`;
    }, [filterLocations, filterAuthorities]);

    const renderActiveMap = () => {
        const props = {
            fetchData: fetchData,
            addFiltersToPath: addFiltersToPath,
            filterLocations: filterLocations,
            filterAuthorities: filterAuthorities,
            changePageToInfo,
            viewState: viewState,
            setViewState: setViewState,
        };
        switch (activeMap) {
            case 'HEATMAP':
                return <HeatMap {...props}
                                mapStyle={isDarkMode ? MAP_STYLES.withLabels.dark : MAP_STYLES.withLabels.light}/>;
            case 'HEXAGONMAP':
                return <HexagonMap {...props} mapStyle={MAP_STYLES.noLabels.dark}/>;
            case 'ICONMAP':
                return <IconMap {...props}
                                mapStyle={isDarkMode ? MAP_STYLES.withLabels.dark : MAP_STYLES.withLabels.light}/>;
            default:
                return null;
        }
    };

    const handleFilterMenuIconClick = () => {
        setShowFilterMenu(!showFilterMenu);
    }

    return (
        <div>
            {renderActiveMap()}
            <FilterSideBar
                apiBaseUrl={apiBaseUrl}
                opened={showFilterMenu}
                filterLocations={filterLocations}
                setFilterLocations={setFilterLocations}
                filterAuthorities={filterAuthorities}
                setFilterAuthorities={setFilterAuthorities}
                setShowFilterMenu={setShowFilterMenu}/>
            <IconButton
                aria-label="open drawer"
                onClick={handleFilterMenuIconClick}
                edge="start"
                style={{
                    marginLeft: 10,
                    marginTop: 'var(--app-bar-height)', //height of appbar
                    backgroundColor: theme.palette.background.default,
                    borderRadius: '50%',
                    padding: '13px',
                    boxShadow: '0px 8px 12px rgba(0, 0, 0, 0.5)',
                    position: 'fixed',
                    bottom: isMobile ? 10 : 'auto', // Only apply "bottom" when isMobile is true
                    top: isMobile ? 'auto' : 10,     // Only apply "top" when isMobile is false
                }}
            >
                <FilterAltIcon style={{color: theme.palette.mode === 'light' ? 'black' : 'white'}}/>
            </IconButton>
            <ToastContainer
                position="top-center"
                autoClose={1500}
                limit={2}
                hideProgressBar={false}
                newestOnTop={false}
                closeOnClick
                rtl={false}
                pauseOnFocusLoss={false}
                draggable
                pauseOnHover={false}
                theme="light"
            />
        </div>
    );
}

export default Map;