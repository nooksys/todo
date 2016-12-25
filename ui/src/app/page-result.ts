export class PageResult<T> {

    pageNo: number
    pageSize: number
    totalRecords: number
    items: T[]
}