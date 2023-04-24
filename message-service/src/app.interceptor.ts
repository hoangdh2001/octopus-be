import {
  CallHandler,
  ExecutionContext,
  NestInterceptor,
  RequestTimeoutException,
} from '@nestjs/common';
import {
  catchError,
  Observable,
  retry,
  tap,
  throwError,
  timeout,
  TimeoutError,
} from 'rxjs';

export class AppInterceptor implements NestInterceptor {
  intercept(
    context: ExecutionContext,
    next: CallHandler<any>,
  ): Observable<any> | Promise<Observable<any>> {
    console.log('Before...');

    const [req] = context.getArgs();

    console.log(`${req.method}: ${req.route?.path}`);
    console.log(`query: ${JSON.stringify(req.query)}`);
    console.log(`params: ${JSON.stringify(req.params)}`);
    console.log(`body: ${JSON.stringify(req.body)}`);

    const now = Date.now();
    return next.handle().pipe(
      timeout(5000),
      catchError((err) => {
        if (err instanceof TimeoutError)
          return throwError(() => new RequestTimeoutException());
        return throwError(() => err);
      }),
      tap(() => console.log(`After... ${Date.now() - now}ms`)),
    );
  }
}
