import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';

@Component({
  selector: 'app-payment-success',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './payment-success.component.html',
  styleUrl: './payment-success.component.css'
})
export class PaymentSuccessComponent {
  private route = inject(ActivatedRoute);

  // orderId comes from the success_url the backend builds (see
  // PaymentServiceImpl) - it's just for a nicer "view your order" link here,
  // not proof of payment. The webhook (server-to-server, signature-verified)
  // is what actually marks the order PAID - see backend README.
  readonly orderId = this.route.snapshot.queryParamMap.get('orderId');
}
