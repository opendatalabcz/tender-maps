import {
    Divider,
    Link,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow, useTheme
} from "@mui/material";
import Box from "@mui/material/Box";

function truncate(str, n) {
    return (str.length > n) ? str.slice(0, n - 1) + '…' : str;
}

function getProcurementUrl(procurementSystemNumber) {
    return "https://nen.nipez.cz/verejne-zakazky/detail-zakazky/" + procurementSystemNumber.replaceAll("/", "-");
}

function renderPrice(price) {
    if (price === null || price === 'undefined') {
        return "N/A CZK";
    } else {
        return price.toString() + " CZK";
    }
}

function buildLinkToCompanyDetail(organisationId) {
    return "https://or.justice.cz/ias/ui/rejstrik-$firma?ico=" + organisationId;
}

function Tooltip({info, suppliedProcurements, offers}) {
    const {object: companyInfo, x, y} = info;
    const theme = useTheme();

    if (!companyInfo) {
        return null;
    }

    return (
        <Box backgroundColor={theme.palette.background.paper} sx={{
            position: 'absolute',
            z: 9,
            fontSize: 12,
            padding: 1.5,
            minWidth: 200,
            pointerEvents: 'all',
            overflowY: 'auto',
            maxHeight: 300,
            left: x,
            top: y
        }}>
            <h3>
                <Link href={buildLinkToCompanyDetail(companyInfo.organisationId)} target="_blank">
                    {companyInfo.name}
                </Link>
            </h3>
            <TableContainer className="custom-table">
                {suppliedProcurements.length !== 0 ? (
                    <Box>
                        <Table stickyHeader aria-label="sticky table" size="small">
                            <TableHead>
                                <TableRow>
                                    <TableCell>Supplied Tenders</TableCell>
                                    <TableCell align="right">Price</TableCell>
                                </TableRow>
                            </TableHead>
                            <TableBody>
                                {suppliedProcurements.map((row) => (
                                    <TableRow key={row.id} sx={{'&:last-child td, &:last-child th': {border: 0}}}>
                                        <TableCell component="th" scope="row">
                                            <Link href={getProcurementUrl(row.systemNumber)} target="_blank">
                                                {truncate(row.name, 30)}
                                            </Link>
                                        </TableCell>
                                        <TableCell align="right">
                                            {renderPrice(row.contractPrice)}
                                        </TableCell>
                                    </TableRow>
                                ))}
                            </TableBody>
                        </Table>
                        <Divider sx={{borderBottomWidth: 4}}/>
                    </Box>
                ) : (
                    <div/>
                )}

                <Table stickyHeader aria-label="sticky table" size="small">
                    <TableHead>
                        <TableRow>
                            <TableCell>Offers</TableCell>
                            <TableCell align="right">Price</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {offers.map((row) => (
                            <TableRow
                                key={row.id}
                                sx={{'&:last-child td, &:last-child th': {border: 0}}}
                            >
                                <TableCell component="th" scope="row">
                                    <Link href={getProcurementUrl(row.procurement.systemNumber)} target="_blank">
                                        {truncate(row.procurement.name, 30)}
                                    </Link>
                                </TableCell>
                                <TableCell align="right">
                                    {renderPrice(row.price)}
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>
        </Box>
    );
}

export default Tooltip;